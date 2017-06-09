package ch.awae.moba.core.model;

import ch.awae.moba.core.logic.Logic;

final class Button implements Logic {

    private final Sector sector;
    private final int    mask;

    Button(Sector sector, int mask) {
        this.sector = sector;
        this.mask = mask;
    }

    @Override
    public boolean evaluate(Model m) {
        short state = m.buttons.getState(this.sector);
        return ((state & this.mask) & 0x0000ffff) > 0;
    }

}
