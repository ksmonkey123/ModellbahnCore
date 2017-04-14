package ch.awae.moba.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

final class SafeRunner extends Thread {

    private final Runnable         backer;
    private final Logger           logger;
    private final @Nullable String name;

    SafeRunner(Runnable target) {
        this(target, null);
    }

    SafeRunner(Runnable target, @Nullable String name) {
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
