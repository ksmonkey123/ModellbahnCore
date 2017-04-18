package ch.awae.moba.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

final class SafeRunner extends Thread {

    private final Runnable backer;
    private final Logger   logger;
    private final String   name;

    SafeRunner(Runnable target) {
        this(target, null);
    }

    SafeRunner(Runnable target, String name) {
        super();
        this.backer = target;
        this.logger = Utils.getLogger();
        this.name = name;
    }

    @Override
    public void run() {
        try {
            this.backer.run();
        } catch (RuntimeException e) {
            this.logger.log(Level.SEVERE,
                    "Exception caught in asynchronous job '" + this.name + "'", e);
        }
    }

}
