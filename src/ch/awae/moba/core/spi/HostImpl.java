package ch.awae.moba.core.spi;

final class HostImpl implements Host, SPIHost {

	private final Object LOCK = new Object();
	private final SPIChannel channel;

	private volatile short input = 0x0000;
	private volatile short output = 0x0000;
	private volatile byte network = 0x00;
	private volatile boolean updateFlag = false;

	HostImpl(final SPIChannel channel) {
		this.channel = channel;
	}

	@Override
	public short getInput() {
		return this.input;
	}

	@Override
	public byte getNetwork() {
		return this.network;
	}

	@Override
	public void setOutput(short output) {
		synchronized (LOCK) {
			this.output = output;
			this.updateFlag = true;
		}
	}

	@Override
	public SPIChannel getChannel() {
		return this.channel;
	}

	@Override
	public void write(short data, byte network) {
		this.input = data;
		this.network = network;
	}

	@Override
	public short read() {
		synchronized (LOCK) {
			final short value = this.output;
			this.updateFlag = false;
			return value;
		}
	}

	@Override
	public boolean isUpdated() {
		synchronized (LOCK) {
			return this.updateFlag;
		}
	}

}
