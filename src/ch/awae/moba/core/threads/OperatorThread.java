package ch.awae.moba.core.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.util.ThreadRegistry;

public class OperatorThread implements IThreaded {

	private final Logger logger;
	private final List<IOperator> operators;
	private final Model model;
	private volatile @Nullable Thread thread = null;

	public synchronized void registerOperator(IOperator host) {
		operators.add(host);
	}

	public OperatorThread(Model model) throws IOException {
		Logger logger = Logger.getLogger("OperatorThread");
		assert logger != null;
		this.logger = logger;
		this.model = model;
		this.operators = new ArrayList<>();
		ThreadRegistry.register("operator", this);
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
				for (int i = 0; i < operators.size(); i++) {
					@SuppressWarnings("null")
					IOperator operator = operators.get(i);
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
