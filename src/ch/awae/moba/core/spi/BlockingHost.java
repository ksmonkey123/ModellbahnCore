package ch.awae.moba.core.spi;

import java.util.Objects;

import ch.awae.moba.core.model.Sector;

final class BlockingHost implements Host, SPIHost {

    // LOCKS
    private final Object MONITOR = new Object();
    // CONFIGURATION DATA
    private final String     name;
    private final SPIChannel channel;
    private final Sector     sector;
    // VOLATILE DATA
    private volatile short   input      = (short) 0xffff;
    private volatile short   output     = (short) 0x0000;
    private volatile byte    network    = (byte) 0x00;
    private volatile boolean updateFlag = false;

    BlockingHost(final Sector sector, final SPIChannel channel, final String name) {
        this.channel = Objects.requireNonNull(channel);
        this.name = Objects.requireNonNull(name);
        this.sector = Objects.requireNonNull(sector);
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
        synchronized (this.MONITOR) {
            if (this.output != output) {
                this.updateFlag = true;
                this.output = output;
                this.MONITOR.notify();
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
        synchronized (this.MONITOR) {
            // wait for next update
            if (!this.updateFlag)
                try {
                    this.MONITOR.wait();
                } catch (@SuppressWarnings("unused") InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            this.updateFlag = false;
            return this.output;
        }
    }

    @Override
    public boolean isUpdated() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Sector getSector() {
        return this.sector;
    }

}
