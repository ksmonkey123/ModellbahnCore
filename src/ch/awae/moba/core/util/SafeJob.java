package ch.awae.moba.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SafeJob implements Runnable {

    private static final ExecutorService pool   = Executors.newScheduledThreadPool(2);
    private static final Logger          logger = Utils.getLogger();

    private final Runnable backer;
    private final String   name;

    public SafeJob(Runnable backer, String name) {
        this.backer = backer;
        this.name = name;
    }

    @Override
    public void run() {
        if (backer == null)
            logger.warning("Skipping null job '" + this.name + "'");
        try {
            backer.run();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception caught in asynchronous job '" + this.name + "'", e);
        }
    }

    public void issue() {
        pool.execute(this);
    }

}
