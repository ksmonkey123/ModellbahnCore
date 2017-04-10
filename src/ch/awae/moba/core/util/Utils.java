package ch.awae.moba.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Utils {

	private final static Logger logger;

	static {
		Logger l = Logger.getGlobal();
		assert l != null;
		logger = l;
		logger.setLevel(Level.OFF);
	}

	public static Logger getLogger() {
		return logger;
	}

}
