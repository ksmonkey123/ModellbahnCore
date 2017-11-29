package ch.awae.moba.core.model;

import java.util.NoSuchElementException;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.util.Props;

public enum Sector {
    BOTTOM,
    CENTER,
    LEFT,
    RIGHT,
    LIGHT;

    private final static Props host_config = Configs.load("hosts");

    public static Sector getSector(int id) {
        String sector = host_config.get("host_" + id + ".sector");
        if (sector == null)
            throw new NoSuchElementException("no host found for id " + id);
        return Sector.valueOf(sector);
    }

}
