package ch.awae.moba.core.threads;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.spi.Host;

public class InputProcessor extends AThreaded {

    final Host   host;
    final Sector sector;

    public InputProcessor(final Host host, final Sector sector) {
        super("inpt_proc_" + host.getName());
        this.host = host;
        this.sector = sector;
    }

    @Override
    protected void step() {
        Model.buttons().setState(InputProcessor.this.sector, host.read());
    }

}
