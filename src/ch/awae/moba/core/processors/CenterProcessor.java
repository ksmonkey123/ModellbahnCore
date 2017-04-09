package ch.awae.moba.core.processors;

import ch.awae.moba.core.model.CenterBlock;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.spi.Host;

public class CenterProcessor extends HostProcessor {

	private final CenterBlock block;

	public CenterProcessor(Model model) {
		super(model);
		this.block = model.centerBlock;
	}

	@Override
	public void process(Host host) {
		// read
		short state = host.read();
		this.block.button_1a.setState((state & 0x0001) > 0);
		this.block.button_1b.setState((state & 0x0002) > 0);
		this.block.button_2a.setState((state & 0x0004) > 0);
		this.block.button_2b.setState((state & 0x0008) > 0);
		this.block.button_3a.setState((state & 0x0010) > 0);
		this.block.button_3b.setState((state & 0x0020) > 0);
		this.block.button_4a.setState((state & 0x0040) > 0);
		this.block.button_4b.setState((state & 0x0080) > 0);
		this.block.button_reset.setState((state & 0x0100) > 0);
		// write

		byte network = (byte) ((this.block.switch_4.getState().code << 6) | (this.block.switch_3.getState().code << 4)
				| (this.block.switch_2.getState().code << 2) | (this.block.switch_1.getState().code) & 0x000000ff);

		host.write((short) (network & 0x00ff), network);
	}
}
