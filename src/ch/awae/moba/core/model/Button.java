package ch.awae.moba.core.model;

import java.util.Objects;

import ch.awae.moba.core.logic.Logic;

/**
 * Represents a button as a {@link Logic} instance for directly testing if a
 * button is currently pressed. A Button is given by a {@link Sector} instance
 * and an 16-bit mask (given as an integer instead of a short to enable unsigned
 * evaluation). Each enabled bit indicates a pin where the button is connected.
 * Therefore only one (1) bit may ever be set (i.e. the value must be a power of
 * two).
 * 
 * @author Andreas Wälchli
 * 
 * @see Logic
 */
final class Button implements Logic {

    private final Sector sector;
    private final int    mask;
    private final Model  model;

    /**
     * Creates a new Button instance
     * 
     * @param sector
     *            the sector where the button is located. may not be
     *            {@code null}
     * @param mask
     *            the pin mask where the button is located. must be a power of
     *            two in the range [2<sup>0</sup>, 2<sup>15</sup>]. The mask for
     *            the button with index x is 2<sup>x</sup>
     * 
     * @throws NullPointerException
     *             the {@code sector} is {@code null}
     * @throws IllegalArgumentException
     *             the {@code mask} is invalid
     */
    Button(Sector sector, int mask) {
        this.sector = sector;
        this.mask = mask;
        this.model = Model.getInstance();
        assertInvariant();
    }

    /**
     * Verifies that all class invariants have been met.
     * 
     * @throws NullPointerException
     *             the {@code sector} is {@code null}
     * @throws IllegalArgumentException
     *             the {@code mask} is invalid
     */
    private void assertInvariant() {
        Objects.requireNonNull(sector, "sector may not be null");
        // check if mask is valid
        for (int i = 0; i < 16; i++) {
            int power = 1 << i;
            if (mask == power)
                return;
        }
        throw new IllegalArgumentException(
                "mask must be a positive power of two in the range [2^0, 2^15]! found: " + mask);

    }

    @Override
    public boolean evaluate() {
        short state = model.buttons.getState(this.sector);
        return ((state & this.mask) & 0x0000ffff) > 0;
    }

}
