package ch.awae.moba.core.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.logic.LogicGroup;
import ch.awae.moba.core.util.Utils;

public class ButtonProvider {

    private static final HashMap<Sector, Properties> properties = new HashMap<>();

    static {
        try {
            for (Sector sector : Sector.values()) {
                properties.put(sector, Configs.getProperties("buttons." + sector.name()));
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static int getMask(Sector sector, String id) {
        String value = properties.get(sector).getProperty(id);
        if (value == null)
            throw new NullPointerException("no button for id '" + id + "'");
        return Utils.parseInt(value) & 0x0000ffff;
    }

    public static Logic getButton(Sector sector, String id) {
        int mask = getMask(sector, id);
        // check if mask is ok (single '1'-bit)
        if ((mask & (mask - 1)) != 0)
            throw new IllegalArgumentException("Identifier '" + id + "' in sector '" + sector.name()
                    + "' identifies a group, not a button!");
        // mask is ok
        return new Button(sector, mask);
    }

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

    public ButtonProvider(Sector sector) {
        this.sector = sector;
    }

    public Logic button(String id) {
        return getButton(this.sector, id);
    }

    public LogicGroup group(String id) {
        return getGroup(this.sector, id);
    }

}
