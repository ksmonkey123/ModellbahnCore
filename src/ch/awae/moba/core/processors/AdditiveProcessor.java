package ch.awae.moba.core.processors;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.spi.Host;

public class AdditiveProcessor extends HostProcessor {

	private final Sector sector;

	public AdditiveProcessor(Sector sector) {
		this.sector = sector;
	}

	@Override
	public void process(Host host) {
		Model.buttons().setState(this.sector, host.read());

		int data = 0;
		for (Path p :Model.paths().getPaths(this.sector)) {
			data += p.data;
		}

		host.write((short) ((data >> 8) & 0x0000ffff), (byte) (data & 0x000000ff));
	}

}
