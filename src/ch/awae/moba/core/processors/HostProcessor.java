package ch.awae.moba.core.processors;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.spi.Host;

public abstract class HostProcessor {

    public final void performUpdate(Host host) {
        this.process(host);
        synchronized (Model.class) {
            Model.class.notifyAll();
        }
    }

    abstract void process(Host host);

}
