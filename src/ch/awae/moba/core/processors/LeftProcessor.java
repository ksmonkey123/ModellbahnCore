package ch.awae.moba.core.processors;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.spi.Host;

public class LeftProcessor extends HostProcessor {

	public LeftProcessor(Model model) {
		super(model);
	}

	@Override
	public void process(Host host) {
		model.buttons.setState(Sector.RIGHT, host.read());

		int data = 0;
		for (Path p : model.paths.getPaths(Sector.RIGHT)) {
			data += p.data;
		}

		host.write((short) ((data >> 8) & 0x0000ffff), (byte) (data & 0x000000ff));
	}
}
