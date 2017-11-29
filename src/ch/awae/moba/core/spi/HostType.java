package ch.awae.moba.core.spi;

public enum HostType {
    BASIC(true, true),
    LIGHTS(true, false);

    public final boolean input, output;

    HostType(boolean input, boolean output) {
        this.input = input;
        this.output = output;
    }

}
