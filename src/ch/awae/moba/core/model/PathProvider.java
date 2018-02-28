package ch.awae.moba.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.util.Props;
import ch.awae.moba.core.util.Utils;
import ch.awae.utils.functional.T2;

public final class PathProvider {

    private static PathProvider INSTANCE = null;

    public static PathProvider getInstance() {
        if (INSTANCE == null)
            synchronized (PathProvider.class) {
                if (INSTANCE == null)
                    INSTANCE = new PathProvider();
            }
        return INSTANCE;
    }

    // ############### INSTANCE DEFINITION ##################

    private final HashMap<String, Path> path_map;
    private final Props                 path_configuration;
    private final Lock                  READ, WRITE;
    private final Object                CACHE_LOCK   = new Object();
    private volatile boolean            validated    = false;
    private List<Path>                  sorted_cache = null;

    private PathProvider() {
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        READ = rwl.readLock();
        WRITE = rwl.writeLock();
        this.path_map = new HashMap<>();
        this.path_configuration = Configs.load("paths");
        loadPredefinedPaths();
    }

    public Path[] getPaths(String... keys) {
        Path[] list = new Path[keys.length];
        for (int i = 0; i < keys.length; i++) {
            list[i] = getPath(keys[i]);
        }
        return list;
    }

    public List<Path> getSorted() {
        READ.lock();
        try {
            if (!validated) {
                synchronized (CACHE_LOCK) {
                    if (!validated) {
                        List<Path> list = new ArrayList<>();
                        list.addAll(path_map.values());
                        Collections.sort(list, (a, b) -> a.title.compareTo(b.title));
                        sorted_cache = Collections.unmodifiableList(list);
                        validated = true;
                    }
                }
            }
            return sorted_cache;
        } finally {
            READ.unlock();
        }
    }

    public Path getPath(String key) {
        READ.lock();
        try {
            return path_map.get(key);
        } finally {
            READ.unlock();
        }
    }

    private void put(String key, Path path) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(path);
        WRITE.lock();
        try {
            if (path_map.containsKey(key))
                throw new IllegalArgumentException("path key is already used: " + key);
            path_map.put(key, path);
            validated = false;
        } finally {
            WRITE.unlock();
        }
    }

    private void loadPredefinedPaths() {
        for (T2<String, String> entry : path_configuration.getAll()) {
            Path p = createFromProperty(entry._1, entry._2);
            put(entry._1, p);
        }
    }

    private Path createFromProperty(String key, String value) {
        String[] sections = value.split("_");
        if (sections.length != 4)
            throw new IllegalArgumentException("path definition requires 4 segments");
        // parse segments
        Sector sector = Sector.getSector(Utils.parseInt(sections[0]));
        int priority = Utils.parseInt(sections[1]);
        int data = Utils.parseInt(sections[2]);
        int mask = Utils.parseInt(sections[3]);
        // create path
        return new Path(key, sector, mask, data, priority);
    }
}
