package ch.awae.moba.core.spi;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.processors.HostProcessor;
import ch.awae.moba.core.threads.SPIThread;

public final class HostFactory {

	private HostFactory() {
		throw new AssertionError("unreachable");
	}

	public static Host createHost(SPIChannel channel, SPIThread thread, HostProcessor processor,
			@Nullable String name) {
		final HostImpl host = new HostImpl(channel, name, processor);
		thread.registerHost(host);
		return host;
	}

}
