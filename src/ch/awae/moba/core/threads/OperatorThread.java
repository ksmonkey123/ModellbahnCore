package ch.awae.moba.core.threads;

import java.util.logging.Logger;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.Utils;

public class OperatorThread implements IThreaded {

    final Logger logger = Utils.getLogger();
    final Model  model;

    private volatile Thread thread = null;

    public OperatorThread(Model model) {
        assert this.logger != null;
        this.model = model;
        Registries.threads.register("operator", this);
    }

    @Override
    public boolean isActive() {
        return this.thread != null;
    }

    @Override
    public synchronized void start() {
        if (this.thread != null)
            throw new IllegalStateException("already running");
        this.logger.info("starting thread");
        Thread t = new Loop();
        t.start();
        this.thread = t;
    }

    @Override
    public synchronized void stop() throws InterruptedException {
        Thread t = this.thread;
        if (t == null)
            throw new IllegalStateException("already halted");
        this.logger.info("stopping thread");
        this.thread = null;
        t.interrupt();
        t.join();
        this.logger.info("thread stopped");
    }

    private class Loop extends Thread {

        Loop() {
            super();
        }

        @Override
        public void run() {
            loop: while (!this.isInterrupted()) {
                for (String name : Registries.operators.getNames()) {
                    IOperator operator = Registries.operators.get(name);
                    if (operator != null && operator.isActive())
                        operator.update();
                    if (this.isInterrupted())
                        break loop;
                }
                try {
                    synchronized (OperatorThread.this.model) {
                        OperatorThread.this.model.wait();
                    }
                } catch (InterruptedException e) {
                    OperatorThread.this.logger.warning(e.toString());
                    break loop;
                }
            }
        }

    }

}
