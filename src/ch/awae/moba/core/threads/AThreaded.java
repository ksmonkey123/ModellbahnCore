package ch.awae.moba.core.threads;

import java.util.logging.Logger;

import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.SymbolProvider;
import ch.awae.moba.core.util.Utils;

public abstract class AThreaded implements IThreaded {

    protected final Logger logger = Utils.getLogger();

    @Override
    public boolean isActive() {
        return this.thread != null;
    }

    private Thread       thread;
    private final String name;

    public AThreaded(String name) {
        if (name != null && name.isEmpty())
            throw new IllegalArgumentException("name may not be empty");
        this.name = name != null ? name : SymbolProvider.getInstance().next();
        Registries.threads.register(this.name, this);
    }

    @Override
    public synchronized void start() {
        if (this.thread != null)
            throw new IllegalStateException(this.name + " already running");
        this.logger.info("starting thread " + this.name);
        Thread t = new Thread(this::run, this.name);
        t.start();
        this.thread = t;
    }

    @Override
    public synchronized void stop() throws InterruptedException {
        Thread t = this.thread;
        if (t == null)
            throw new IllegalStateException(this.name + " already halted");
        this.logger.info("stopping thread " + this.name);
        t.interrupt();
        t.join();
        this.thread = null;
        this.logger.info("thread " + this.name + " stopped");
    }

    protected void run() {
        while (!this.thread.isInterrupted())
            try {
                this.step();
            } catch (@SuppressWarnings("unused") InterruptedException e) {
                this.logger.warning("terminating thread " + this.name + " due to interrupt");
                return;
            } catch (RuntimeException rex) {
                StringBuilder b = new StringBuilder(
                        "terminating thread " + this.name + " due to error:\n");
                b.append(rex.toString() + "\n");
                for (StackTraceElement e : rex.getStackTrace()) {
                    b.append(e.toString() + "\n");
                }
                this.logger.severe(b.toString());
                this.thread = null;
            }
    }

    protected abstract void step() throws InterruptedException;

}
