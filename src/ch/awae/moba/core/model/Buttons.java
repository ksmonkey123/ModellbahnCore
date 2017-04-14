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

	public boolean getState(ButtonMapping mapping) {
		short state = getState(mapping.sector);
		return ((state >> mapping.index) & 0x00000001) == 1;
	}

	public boolean getState(String button) {
		ButtonMapping mapping = ButtonMapping.byTitle(button);
		if (mapping != null)
			return getState(mapping);
        throw new IllegalArgumentException("unkown button: " + button);
	}

}
