package ch.awae.moba.core.spi;

final class HostImpl implements Host, SPIHost {

    private final Object     LOCK = new Object();
    private final SPIChannel channel;
    private final String     name;

    private volatile short   input      = (short) 0xffff;
    private volatile short   output     = (short) 0x0000;
    private volatile short   output_raw = (short) 0x0000;
    private volatile byte    network    = (byte) 0x00;
    private volatile boolean updateFlag = false;

    HostImpl(final SPIChannel channel, final String name) {
        this.channel = channel;
        this.name = name;
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
        synchronized (this.LOCK) {
            if ((this.output_raw ^ output) == 0) {
                this.output = output;
                this.updateFlag = true;
            }
            this.output_raw = output;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    @Override
    public SPIChannel getChannel() {
        return this.channel;
    }

    @Override
    public void write(short displayData, byte networkData) {
        this.input = displayData;
        this.network = networkData;
    }

    @Override
    public short read() {
        synchronized (this.LOCK) {
            final short value = this.output;
            this.updateFlag = false;
            return value;
        }
    }

    @Override
    public boolean isUpdated() {
        synchronized (this.LOCK) {
            return this.updateFlag;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

}
