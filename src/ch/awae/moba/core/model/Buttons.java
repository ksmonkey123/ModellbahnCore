package ch.awae.moba.core.model;

public class Buttons {

    private final short[] map;

    public Buttons() {
        this.map = new short[Sector.values().length];
    }

    public void setState(Sector sector, short values) {
        this.map[sector.ordinal()] = values;
    }

    public short getState(Sector sector) {
        return this.map[sector.ordinal()];
    }

}
