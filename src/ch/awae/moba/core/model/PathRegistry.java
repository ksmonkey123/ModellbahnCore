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
		if (paths.contains(path))
			logger.info("path '" + path + "' already registered");
		for (int index = 0; index < paths.size(); index++) {
			@SuppressWarnings("null")
			Path p = paths.get(index);
			if (path.collides(p)) {
				logger.info("discarding path '" + p.title + "'");
				paths.remove(index);
				index--;
			}
		}

		logger.info("registering path '" + path.title + "'");
		paths.add(path);

	}

	@SuppressWarnings("null")
	public synchronized List<Path> getAllPaths() {
		return Collections.unmodifiableList(this.paths);
	}

	public synchronized void unregister(Path path) {
		if (paths.remove(path))
			logger.info("discarding path '" + path.title + "'");
		else
			logger.info("path '" + path.title + "' not registered");
	}

	public synchronized List<Path> getPaths(Sector sector) {
		List<Path> result = new ArrayList<Path>();
		for (Path p : paths)
			if (p.sector == sector)
				result.add(p);
		return result;
	}

	public boolean isRegistered(@Nullable Path p) {
		if (p == null)
			return false;
		return paths.contains(p);
	}

}
