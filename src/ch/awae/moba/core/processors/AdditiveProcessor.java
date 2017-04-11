package ch.awae.moba.core.processors;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.spi.Host;

public class AdditiveProcessor extends HostProcessor {

	private final Sector sector;

	public AdditiveProcessor(Model model, Sector sector) {
		super(model);
		this.sector = sector;
	}

	@Override
	public void process(Host host) {
		model.buttons.setState(sector, host.read());

		int data = 0;
		for (Path p : model.paths.getPaths(sector)) {
			data += p.data;
		}

		host.write((short) ((data >> 8) & 0x0000ffff), (byte) (data & 0x000000ff));
	}

}
