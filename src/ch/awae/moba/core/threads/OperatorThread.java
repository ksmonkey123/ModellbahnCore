package ch.awae.moba.core.threads;

import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.Utils;

public class OperatorThread implements IThreaded {

	private final Logger logger = Utils.getLogger();
	private final Model model;
	private volatile @Nullable Thread thread = null;

	public OperatorThread(Model model) {
		assert logger != null;
		this.model = model;
		Registries.threads.register("operator", this);
	}

	@Override
	public boolean isActive() {
		return thread != null;
	}

	public synchronized void start() {
		if (this.thread != null)
			throw new IllegalStateException("already running");
		logger.info("starting thread");
		Thread t = new Loop();
		t.start();
		this.thread = t;
	}

	public synchronized void stop() throws InterruptedException {
		Thread t = this.thread;
		if (t == null)
			throw new IllegalStateException("already halted");
		logger.info("stopping thread");
		this.thread = null;
		t.interrupt();
		t.join();
		logger.info("thread stopped");
	}

	private class Loop extends Thread {

		@Override
		public void run() {
			loop: while (!this.isInterrupted()) {
				for (String name : Registries.operators.getNames()) {
					IOperator operator = Registries.operators.get(name);
					if (operator != null && operator.isActive())
						operator.update(model);
					if (this.isInterrupted())
						break loop;
				}
				try {
					synchronized (model) {
						model.wait();
					}
				} catch (InterruptedException e) {
					break loop;
				}
			}
		}

	}

}
