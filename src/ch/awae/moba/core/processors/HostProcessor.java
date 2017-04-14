package ch.awae.moba.core.processors;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.spi.Host;

public abstract class HostProcessor {

	protected final Model model;

	public HostProcessor(Model model) {
		this.model = model;
	}

	public final void performUpdate(Host host) {
		this.process(host);
		synchronized (this.model) {
			this.model.notifyAll();
		}
	}

	abstract void process(Host host);

}
