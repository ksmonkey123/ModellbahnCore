package ch.awae.moba.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BatchLog {

    private final Logger logger;
    private final String title;
    private final Level  level;

    private StringBuilder builder;

    public BatchLog(Logger logger, Level level, String title) {
        this.logger = logger;
        this.level = level;
        this.title = title;
        init();
    }

    private void init() {
        builder = new StringBuilder(title);
    }

    public synchronized void flush() {
        logger.log(level, builder.toString());
        init();
    }

    public synchronized void log(String message) {
        builder.append("\n||  " + message);
    }

}
