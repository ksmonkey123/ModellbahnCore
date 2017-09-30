package ch.awae.moba.core.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.logic.LogicGroup;
import ch.awae.moba.core.util.Utils;

/**
 * Provides Logic instances representing buttons from property files. Also
 * support direct creation of logic groups: groups specified in the property
 * files can be directly loaded.
 * 
 * The static methods require the sector and the identifier to be given. This
 * class can however also be instantiated using {@link #ButtonProvider(Sector)}.
 * The instance methods do not require the sector to be given since the value
 * from the constructor is filled in automatically. This is especially useful if
 * several buttons from the same sector are required.
 * 
 * @author Andreas Wälchli
 */
public class ButtonProvider {

    
    
    private static final HashMap<Sector, Properties> properties = new HashMap<>();

    static {
        try {
            // load the property file for each sector
            for (Sector sector : Sector.values()) {
                properties.put(sector, Configs.getProperties("buttons." + sector.name()));
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * extracts the mask for a given {@code sector,id} combination
     * 
     * @param sector
     *            the sector the mask is defined in. may not be {@code null}
     * @param id
     *            the mask identifier within the sector. may not be {@code null}
     * @return the integer mask for the given identifiers
     * 
     * @throws NullPointerException
     *             if any parameter is {@code null} or no mask is found for the
     *             given identifiers
     * @throws NumberFormatException
     *             if the entry in the properties file cannot be parsed into an
     *             integer
     */
    private static int getMask(Sector sector, String id) {
        Objects.requireNonNull(sector, "sector may not be null");
        Objects.requireNonNull(id, "id may not be null");

        String value = properties.get(sector).getProperty(id);
        if (value == null)
            throw new NullPointerException("no button for id '" + id + "'");
        return Utils.parseInt(value) & 0x0000ffff;
    }

    /**
     * Provides a {@link Logic} instance representing the button represented by
     * the given {@code id} in the given {@code sector}.
     * 
     * @param sector
     *            the sector the button is located in. may not be {@code null}
     * @param id
     *            the button id within the sector. may not be {@code null} must
     *            represent a button and may not represent a group. An id is
     *            considered a group id if the associated button mask has more
     *            than one set bit. i.e. a button mask must be a positive power
     *            of two.
     * @return a {@link Logic} instance representing the given button
     * @throws NullPointerException
     *             the {@code sector} or the {@code id} is {@code null}
     * @throws NullPointerException
     *             the button id references no existing button mask
     * @throws IllegalArgumentException
     *             the id is invalid
     * @throws NumberFormatException
     *             if the mask entry cannot be parsed into an integer
     */
    public static Logic getButton(Sector sector, String id) {
        int mask = getMask(sector, id);
        // check if mask is ok (single '1'-bit)
        if ((mask & (mask - 1)) != 0)
            throw new IllegalArgumentException("Identifier '" + id + "' in sector '" + sector.name()
                    + "' identifies a group, not a button!");
        // mask is ok
        return new Button(sector, mask);
    }

    /**
     * Provides a {@link LogicGroup} instance representing the button group
     * represented by the given {@code id} in the given {@code sector}. If the
     * {@code id} references a button a {@link LogicGroup} containing this
     * button.
     * 
     * @param sector
     *            the sector the button group is located in. may not be
     *            {@code null}
     * @param id
     *            the button group id within the sector. May represent a button
     *            or a button group. may not be {@code null}
     * @return a {@link LogicGroup} instance representing the given button
     *         group.
     * @throws NullPointerException
     *             the {@code sector} or the {@code id} is {@code null}
     * @throws NullPointerException
     *             the button id references no existing button or button group
     *             mask
     * @throws IllegalArgumentException
     *             the id is invalid
     * @throws NumberFormatException
     *             if the mask entry cannot be parsed into an integer
     */
    public static LogicGroup getGroup(Sector sector, String id) {
        int mask = getMask(sector, id);

        Logic[] result = new Logic[countOnes(mask)];

        int index = 0;
        for (int i = 0; i < 16; i++) {
            if (((mask >> i) & 0x00000001) == 1) {
                int partialMask = mask & (1 << i);
                result[index] = new Button(sector, partialMask);
                index++;
            }
        }

        return new LogicGroup(result);
    }

    private static int countOnes(int number) {
        int num = number;
        int count = 0;
        for (int i = 0; i < 16; i++) {
            if ((num & 0x00000001) == 1)
                count++;
            num >>= 1;
        }
        return count;
    }

    // SECTOR PRESET INSTANCE

    private final Sector sector;

    /**
     * Creates a new {@link ButtonProvider} instance with a given {@link Sector}
     * value. This value is used as a default sector value. Use the instance
     * methods {@link #button(String)} and {@link #group(String)}. These methods
     * are identical in operation to the static methods
     * {@link #getButton(Sector, String)} and {@link #getGroup(Sector, String)}
     * respectively but use the given {@code sector} as the sector for those
     * requests.
     * 
     * @param sector
     *            the {@link Sector} to be used. may not be null
     * @throws NullPointerException
     *             the {@code sector} parameter is null
     */
    public ButtonProvider(Sector sector) {
        this.sector = Objects.requireNonNull(sector, "the sector may not be null");
    }

    /**
     * Requests the button referenced by the given {@code id}. This method is
     * functionally identical to {@link #getButton(Sector, String)} but uses the
     * sector value given in the instance constructor.
     * 
     * @param id
     *            the button id. Must be non-null and a valid button id
     * @return a {@link Logic} instance referencing the given button
     * @see #getButton(Sector, String)
     * @throws NullPointerException
     *             the {@code id} is null
     * @throws NullPointerException
     *             the button id references no existing button mask
     * @throws IllegalArgumentException
     *             the id is invalid
     * @throws NumberFormatException
     *             if the mask entry cannot be parsed into an integer
     */
    public Logic button(String id) {
        return getButton(this.sector, id);
    }

    /**
     * Requests the button group referenced by the given {@code id}. This method
     * is functionally identical to {@link #getGroup(Sector, String)} but uses
     * the sector value given in the instance constructor.
     * 
     * @param id
     *            the button group id. Must be non-null and a valid button or
     *            button group id
     * @return a {@link Logic} instance referencing the given button group
     * @see #getGroup(Sector, String)
     * 
     * @throws NullPointerException
     *             the {@code id} is null
     * @throws NullPointerException
     *             the button id references no existing button or button group
     *             mask
     * @throws IllegalArgumentException
     *             the id is invalid
     * @throws NumberFormatException
     *             if the mask entry cannot be parsed into an integer
     */
    public LogicGroup group(String id) {
        return getGroup(this.sector, id);
    }

}
