package ch.awae.moba.core.threads;

import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.processors.HostProcessor;
import ch.awae.moba.core.spi.Host;
import ch.awae.moba.core.util.ThreadRegistry;

public class ProcessorThread implements IThreaded {

	private final Logger logger;

	private final Host host;
	private final HostProcessor processor;

	private @Nullable Thread thread;

	public ProcessorThread(final Host host, final HostProcessor processor) {
		this.host = host;
		this.processor = processor;
		Logger logger = Logger.getLogger("ProcessorThread (" + host.getName() + ")");
		assert logger != null;
		this.logger = logger;
		ThreadRegistry.register("processor_" + host.getName(), this);
	}

	public synchronized void start() {
		if (this.thread != null)
			throw new IllegalStateException("already running");
		this.logger.info("starting thread");
		Thread t = new Loop();
		t.start();
		this.thread = t;
	}

	public synchronized void stop() throws InterruptedException {
		Thread t = this.thread;
		if (t == null)
			throw new IllegalStateException("already halted");
		this.logger.info("stopping thread");
		t.interrupt();
		t.join();
		this.thread = null;
		this.logger.info("thread stopped");
	}

	private class Loop extends Thread {

		@Override
		public void run() {
			loop: while (!this.isInterrupted()) {
				if (host.isUpdated()) {
					ProcessorThread.this.processor.performUpdate(ProcessorThread.this.host);
				} else {
					try {
						synchronized (host) {
							host.wait();
						}
					} catch (InterruptedException e) {
						break loop;
					}
				}
			}
		}

	}

}
