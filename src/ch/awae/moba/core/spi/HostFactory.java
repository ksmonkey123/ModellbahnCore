package ch.awae.moba.core.spi;

import ch.awae.moba.core.util.Pair;

public final class HostFactory {

	private HostFactory() {
		throw new AssertionError("unreachable");
	}

	public static Pair<SPIHost, Host> createHost(SPIChannel channel, String name) {
		final HostImpl host = new HostImpl(channel, name);
		return new Pair<>(host, host);
	}

}
