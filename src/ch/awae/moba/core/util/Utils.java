package ch.awae.moba.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;

/**
 * Collection of utility methods
 * 
 * @author Andreas Wälchli
 */
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

    /**
     * provides the Logger instance to be used by all components.
     * 
     * @return the Logger instance
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * performs a system reboot.
     * 
     * The reboot is indicated by setting the error state on all sectors. (All
     * bicolor LEDs are yellow) This blocks any further actions. If the reboot
     * fails for any reason, this is indicated as a fatal exception. (All
     * bicolor LEDs are green). The fatal exception is followed by a forceful
     * exit of the application with error code -1 {@code 0xffffffff}.
     * 
     * @param model
     *            the model to apply the indications to
     */
    public static void doReboot(Model model) {
        logger.info("initiating system reset");
        model.paths.register(Path.SYSTEM_ERROR_B);
        model.paths.register(Path.SYSTEM_ERROR_C);
        model.paths.register(Path.SYSTEM_ERROR_L);
        model.paths.register(Path.SYSTEM_ERROR_R);
        try {
            Runtime.getRuntime().exec("sudo reboot");
        } catch (Exception e) {
            logger.severe(e.toString());
            model.paths.register(Path.SYSTEM_FATAL_B);
            model.paths.register(Path.SYSTEM_FATAL_C);
            model.paths.register(Path.SYSTEM_FATAL_L);
            model.paths.register(Path.SYSTEM_FATAL_R);
            async(() -> {
                sleep(1000);
                System.exit(-1);
            });
        }
    }

    /**
     * sleeps for the given number of milliseconds.
     * 
     * This method uses {@link Thread#sleep(long)} in the background. Any
     * {@link InterruptedException} thrown is caught and logged with log level
     * '{@code WARNING}'. The exception is consumed and not rethrown.
     * 
     * @param millis
     *            the number of milliseconds to wait for
     * @return {@code true} if the sleep finished normally, {@code false} if it
     *         was interrupted.
     * @throws IllegalArgumentException
     *             if the {@code millis} argument is negative.
     * @see Thread#sleep(long)
     */
    public static boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            logger.warning(e.toString());
            return false;
        }
    }

    /**
     * Performs an asynchronous operation. Any exceptions thrown by the
     * operation are caught and logged.
     * 
     * @param runnable
     *            the operation to perform
     * @param name
     *            an optional name listed in the logs
     */
    public static void async(Runnable runnable, @Nullable String name) {
        new SafeRunner(runnable, name).start();
    }

    /**
     * Performs an asynchronous operation. This is a convenience method for
     * {@link Utils#async(Runnable, String) Utils.async(runnable, null)}
     * 
     * @param runnable
     *            the operation to perform
     * @see Utils#async(Runnable, String)
     */
    public static void async(Runnable runnable) {
        async(runnable, null);
    }

    /**
     * Reads a properties file
     * 
     * @param file
     *            the file to read
     * @return the properties
     * @throws IllegalArgumentException
     *             if the {@code file} could not be located
     * @throws IOException
     *             if an error occurred while reading the file
     */
    public static Properties getProperties(String file) throws IOException {
        try (InputStream in = Utils.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null)
                throw new IllegalArgumentException("file not found: " + file);

            Properties p = new Properties();
            p.load(in);
            return p;
        }
    }

}
