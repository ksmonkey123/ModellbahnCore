package ch.awae.moba.core.spi;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Sector;

public class LightsHost extends BlockingHost {

    private final static int RUNS = 2;

    private int  run;
    private int  decoder;
    private int  block;
    private byte cache;

    public LightsHost(Sector sector, SPIChannel channel, String name) {
        super(sector, channel, name);
        decoder = 7;
        block = 7;
        run = RUNS - 1;
        cache = 0;
    }

    @Override
    public short getInput() {
        long deltaT = System.currentTimeMillis() - Model.getLastUpdate();
        int data = Model.isStealthMode() ? 0x0002 : 0x0000;
        data += (deltaT < 50 ? 1 : 0);
        return (short) (data & 0x000000ff);
    }

    @Override
    public byte getNetwork() {
        return cache;
    }

    @Override
    public void next() {
        run = (run + 1) % RUNS;
        if (run == 0) {
            decoder = (decoder + 1) % 8;
            if (decoder == 0) {
                block = (block + 1) % 8;
            }
            cache = Model.lights().getOptimal(decoder, block);
        }
    }

}
