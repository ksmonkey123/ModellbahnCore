package ch.awae.moba.core.threads;

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.util.Controllable;
import ch.awae.moba.core.util.Utils;

/**
 * Base class for stoppable and resumable threads
 * 
 * @author Andreas Wälchli
 *
 */
public abstract class ThreadBase implements Controllable {

    private final Logger logger = Utils.getLogger();

    private volatile RunnerThread thread = null;

    @Override
    public synchronized void start() {
        if (this.thread != null) {
            this.logger.warning("Thread is already running!");
        } else {
            this.thread = new RunnerThread();
            this.thread.start();
        }
    }

    @Override
    public synchronized void halt() {
        if (this.thread == null) {
            this.logger.warning("Thread is already halted!");
        } else {
            this.thread.doStop();
            this.thread.interrupt();
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                this.logger.log(Level.INFO, "interrupted while joining stopping thread", e);
            }
            this.thread = null;
        }
    }

    protected abstract void step();

    protected abstract void pause() throws InterruptedException;

    private class RunnerThread extends Thread {

        @SuppressWarnings("hiding")
        private final Logger     logger;
        private volatile boolean active;

        public RunnerThread() {
            this.active = false;
            this.logger = Utils.getLogger();
        }

        public void doStop() {
            this.active = false;
        }

        @Override
        public void run() {
            this.active = true;
            this.logger.info("starting thread");
            loop: while (this.active) {
                try {
                    ThreadBase.this.pause();
                    if (!this.active)
                        break loop;
                    ThreadBase.this.step();
                } catch (InterruptedException ie) {
                    this.logger.log(Level.INFO, "stopping due to interrupt", ie);
                    break loop;
                } catch (RuntimeException rte) {
                    this.logger.log(Level.SEVERE, "stopping due to runtime exception", rte);
                    break loop;
                }
            }
            this.active = false;
            this.logger.info("stopping thread");

        }
    }

}
