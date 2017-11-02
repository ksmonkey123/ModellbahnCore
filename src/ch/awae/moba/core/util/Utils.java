package ch.awae.moba.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;

/**
 * Collection of utility methods
 * 
 * @author Andreas Wälchli
 */
public final class Utils {

    private final static String REBOOT_COMMAND = "sudo reboot";
    private final static Logger logger;
    private final static Path[] error, fatal;

    static {
        Handler systemOut = new ConsoleHandler();
        systemOut.setLevel(Level.ALL);

        PathProvider pp = PathProvider.getInstance();

        error = pp.getPaths("bottom.system_error", "left.system_error", "right.system_error",
                "center.system_error");
        fatal = pp.getPaths("bottom.fatal_error", "left.fatal_error", "right.fatal_error",
                "center.fatal_error");

        logger = Logger.getLogger("core");
        logger.setLevel(Level.ALL);
        logger.addHandler(systemOut);
        logger.setLevel(Level.INFO);
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
    public static void doReboot() {
        logger.info("initiating system reset");
        for (Path p : error)
            p.issue(true);
        Model.executeCommands();
        try {
            Runtime.getRuntime().exec(REBOOT_COMMAND);
        } catch (Exception e) {
            logger.severe(e.toString());
            for (Path p : fatal)
                p.issue(true);
            Model.executeCommands();
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
    public static void async(Runnable runnable, String name) {
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
        try (InputStream in = Utils.class.getClassLoader()
                .getResourceAsStream("resources/" + file)) {
            if (in == null)
                throw new IllegalArgumentException("file not found: resources/" + file);

            Properties p = new Properties();
            p.load(in);
            return p;
        }
    }

    /**
     * Parses a String into a signed integer.
     * 
     * The following formats are supported:
     * <ul>
     * <li>{@code 0b00011011} represents a binary number</li>
     * <li>{@code 0x0123af6b} represents a hexadecimal number</li>
     * <li>any decimal format supported by {@link Integer#parseInt(String)}</li>
     * </ul>
     * 
     * @param number
     *            the number to parse. may not be null
     * @return the signed integer value of the given String
     * @throws NullPointerException
     *             the {@code number} is null
     * @throws NumberFormatException
     *             the {@code number} cannot be parsed into an integer. See
     *             {@link Integer#parseInt(String, int)} for more information
     * @see Integer#parseInt(String, int)
     */
    public static int parseInt(String number) {
        Objects.requireNonNull(number, "the number may not be null");
        if (number.startsWith("0b"))
            return Integer.parseInt(number.substring(2), 2);
        else if (number.startsWith("0x"))
            return Integer.parseInt(number.substring(2), 16);
        else
            return Integer.parseInt(number, 10);
    }

}
