package ch.awae.moba.core.spi;

public final class HostFactory {

	private HostFactory() {
		throw new AssertionError("unreachable");
	}

	public static Host createHost(SPIChannel channel, SPIGroup group) {
		final HostImpl host = new HostImpl(channel);
		group.registerHost(host);
		return host;
	}

}
