package ch.awae.moba.core.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;

public final class Utils {

	private final static Logger logger;

	static {
		Logger l = Logger.getGlobal();
		assert l != null;
		l.setLevel(Level.ALL);
		logger = l;
		Handler systemOut = new ConsoleHandler();
		systemOut.setLevel(Level.ALL);
		logger.addHandler(systemOut);
		logger.setLevel(Level.SEVERE);
		logger.setUseParentHandlers(false);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void doReboot(Model model) {
		logger.info("initiating system reset");
		model.paths.register(Path.SYSTEM_ERROR_B);
		model.paths.register(Path.SYSTEM_ERROR_C);
		model.paths.register(Path.SYSTEM_ERROR_L);
		model.paths.register(Path.SYSTEM_ERROR_R);
		try {
			Runtime.getRuntime().exec("sudo reboot");
		} catch (IOException e) {
			logger.severe(e.toString());
			model.paths.register(Path.SYSTEM_FATAL_B);
			model.paths.register(Path.SYSTEM_FATAL_C);
			model.paths.register(Path.SYSTEM_FATAL_L);
			model.paths.register(Path.SYSTEM_FATAL_R);
			new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					System.exit(1);
				};
			}.start();
		}
	}

}
