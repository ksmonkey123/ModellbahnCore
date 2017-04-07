package ch.awae.moba.core.spi;

import java.util.ArrayList;

public final class SPIGroup {

	private final ArrayList<SPIHost> hosts;

	public SPIGroup() {
		this.hosts = new ArrayList<>();
	}

	void registerHost(SPIHost host) {
		for (SPIHost h : hosts) {
			if (host.getChannel() == h.getChannel())
				throw new IllegalArgumentException("already have a host for channel " + host.getChannel());
		}
		hosts.add(host);
	}

	public Iterable<SPIHost> getHostList() {
		return this.hosts;
	}

}
