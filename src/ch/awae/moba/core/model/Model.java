package ch.awae.moba.core.model;

import java.util.List;

/**
 * Represents both pressed buttons and active paths
 * 
 * @author Andreas Wälchli
 */
public final class Model {

    public final PathRegistry paths   = new PathRegistry();
    public final Buttons      buttons = new Buttons();

    // ==== METHODS ====

    public short getButtons(final Sector sector) {
        return buttons.getState(sector);
    }

    public boolean isPressed(final Sector sector, final int mask) {
        short state = getButtons(sector);
        return ((state & mask) & 0x0000ffff) > 0;
    }

    public void register(final Path path) {
        this.paths.register(path);
    }

    public void unregister(final Path path) {
        this.paths.unregister(path);
    }

    public List<Path> getPaths(final Sector sector) {
        return paths.getPaths(sector);
    }

    public List<Path> getAllPaths() {
        return paths.getAllPaths();
    }

}
