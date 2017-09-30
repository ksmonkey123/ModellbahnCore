package ch.awae.moba.core.threads;

import java.util.ArrayList;
import java.util.List;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.spi.Host;

public class OutputProcessor extends AThreaded {

    private final long TARGET_UPDATE_DURATION_MILLIS = 20;

    private final Model  model;
    private final Host[] hosts;

    public OutputProcessor(final Model model, final Host[] hosts) {
        super("oupt_proc");
        this.model = model;
        this.hosts = hosts;
    }

    @Override
    protected void step() throws InterruptedException {
        long start = System.currentTimeMillis();
        List<Path> paths = this.getPaths();
        // assemble paths
        int[] data = new int[Sector.values().length];
        for (Path path : paths) {
            int index = path.sector.ordinal();
            data[index] += path.data;
        }
        // write each sector
        for (Host host : hosts) {
            int index = host.getSector().ordinal();
            host.write((short) ((data[index] >> 8) & 0x0000ffff),
                    (byte) (data[index] & 0x000000ff));
        }
        // sleep for a bit (update rate of ca. 50Hz seems reasonable)
        long end = System.currentTimeMillis();
        long Δt = TARGET_UPDATE_DURATION_MILLIS - (end - start);
        if (Δt > 0)
            Thread.sleep(Δt);
    }

    private List<Path> getPaths() {
        synchronized (this.model) {
            return new ArrayList<>(this.model.paths.getAllPaths());
        }
    }

}
