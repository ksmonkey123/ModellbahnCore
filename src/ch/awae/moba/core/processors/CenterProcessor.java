package ch.awae.moba.core.processors;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.spi.Host;

public class CenterProcessor extends HostProcessor {

	public CenterProcessor(Model model) {
		super(model);
	}

	@Override
	public void process(Host host) {
		model.buttons.setState(Sector.CENTER, host.read());

		int data = 0;
		for (Path p : model.paths.getPaths(Sector.CENTER)) {
			data |= p.data;
		}

		host.write((short) ((data >> 8) & 0x000000ff), (byte) (data & 0x000000ff));
	}
}
