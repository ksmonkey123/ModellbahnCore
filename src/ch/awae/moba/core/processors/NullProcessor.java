package ch.awae.moba.core.processors;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.spi.Host;

public class NullProcessor extends HostProcessor {

	public NullProcessor(Model model) {
		super(model);
	}

	@Override
	void process(@NonNull Host host) {
		host.read();
		host.write((short) 0xffff, (byte) 0);
	}

}
