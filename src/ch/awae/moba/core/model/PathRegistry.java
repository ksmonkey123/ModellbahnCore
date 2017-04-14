package ch.awae.moba.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.util.Utils;

public class PathRegistry {

    private final Logger logger = Utils.getLogger();

    private ArrayList<Path> paths = new ArrayList<>();

    public synchronized void register(Path path) {
        if (this.paths.contains(path))
            this.logger.info("path '" + path + "' already registered");
        for (int index = 0; index < this.paths.size(); index++) {
            @SuppressWarnings("null")
            Path p = this.paths.get(index);
            if (path.collides(p)) {
                if (p.forced & !path.forced) {
                    this.logger.warning("cannot discard forced code '" + p.title + "'");
                    return;
                }
                this.logger.info("discarding path '" + p.title + "'");
                this.paths.remove(index);
                index--;
            }
        }

        this.logger.info("registering path '" + path.title + "'");
        this.paths.add(path);

    }

    @SuppressWarnings("null")
    public synchronized List<Path> getAllPaths() {
        return Collections.unmodifiableList(this.paths);
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

    public boolean isRegistered(@Nullable Path p) {
        if (p == null)
            return false;
        return this.paths.contains(p);
    }

}
