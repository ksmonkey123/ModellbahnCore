package ch.awae.moba.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ch.awae.moba.core.util.Utils;

public class PathRegistry {

    private final Logger logger = Utils.getLogger();

    private ArrayList<Path> paths = new ArrayList<>();

    public synchronized void register(Path path) {
        if (this.paths.contains(path)) {
            this.logger.fine("path '" + path.title + "' already registered");
            return;
        }
        final StringBuilder changes = new StringBuilder("path + " + path.title);
        for (int index = 0; index < this.paths.size(); index++) {
            Path p = this.paths.get(index);
            if (path.collides(p)) {
                if (p.priority > path.priority) {
                    this.logger.info(changes.toString());
                    this.logger.warning("cannot discard forced code '" + p.title + "'");
                    return;
                }
                changes.append("\n  | discarding path '" + p.title + "'");
                this.paths.remove(index);
                index--;
            }
        }
        changes.append("\n  | registering path '" + path.title + "'");
        this.logger.info(changes.toString());
        this.paths.add(path);
    }

    public synchronized List<Path> getAllPaths() {
        List<Path> result = new ArrayList<>();
        for (Path p : paths)
            result.add(p);
        return result;
    }

    public synchronized void unregister(Path path) {
        if (this.paths.remove(path))
            this.logger.info("discarding path '" + path.title + "'");
        else
            this.logger.info("path '" + path.title + "' not registered");
    }

    public synchronized List<Path> getPaths(Sector sector) {
        List<Path> result = new ArrayList<>();
        for (Path p : this.paths)
            if (p.sector == sector)
                result.add(p);
        return result;
    }

    public boolean isRegistered(Path p) {
        if (p == null)
            return false;
        return this.paths.contains(p);
    }

}
