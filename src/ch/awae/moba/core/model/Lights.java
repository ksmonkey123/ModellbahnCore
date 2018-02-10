package ch.awae.moba.core.model;

public final class Lights {

    final boolean[] data = new boolean[128];
    final boolean[] tchd = new boolean[128];

    public synchronized void enable(int decoder, short mask) {
        short probe = 0x0001;
        for (int i = 0; i < 16; i++) {
            int id = 8 * decoder + i;
            if ((mask & (probe << i)) > 0)
                if (!data[id])
                    tchd[id] = true;
            data[id] = true;
        }
    }

    public synchronized void disable(int decoder, short mask) {
        short probe = 0x0001;
        for (int i = 0; i < 16; i++) {
            int id = 8 * decoder + i;
            if ((mask & (probe << i)) > 0) {
                if (data[id])
                    tchd[id] = true;
                data[id] = false;
            }
        }
    }

    public synchronized void setState(int decoder, int pin, boolean state) {
        int id = 8 * decoder + pin;
        if (data[id] != state) {
            data[id] = state;
            tchd[id] = true;
        }
    }

    public synchronized byte getOptimal(int decoder, int dflt) {
        for (int i = 0; i < 8; i++) {
            int baseID = 2 * (8 * decoder + i);
            if (tchd[baseID] || tchd[baseID + 1]) {
                // clear touch flag
                tchd[baseID + 0] = false;
                tchd[baseID + 1] = false;
                // get data
                return getData(decoder, i);
            }
        }
        // no priority data. take default series
        return getData(decoder, dflt);
    }

    public synchronized boolean getState(int decoder, int pin) {
        return data[8 * decoder + pin];
    }

    private byte getData(int decoder, int pair) {
        byte result = (byte) (((decoder << 5) & 0x000000e0) | ((pair << 2) & 0x0000001c) & 0xff);
        result += data[2 * (8 * decoder + pair) + 0] ? 1 : 0;
        result += data[2 * (8 * decoder + pair) + 1] ? 2 : 0;
        return result;
    }

}
