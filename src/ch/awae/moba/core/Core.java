package ch.awae.moba.core;

import java.io.IOException;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.AOperator;
import ch.awae.moba.core.processors.AdditiveProcessor;
import ch.awae.moba.core.spi.Host;
import ch.awae.moba.core.spi.HostFactory;
import ch.awae.moba.core.spi.SPIChannel;
import ch.awae.moba.core.spi.SPIHost;
import ch.awae.moba.core.threads.ConsoleThread;
import ch.awae.moba.core.threads.IThreaded;
import ch.awae.moba.core.threads.OperatorThread;
import ch.awae.moba.core.threads.ProcessorThread;
import ch.awae.moba.core.threads.SPIThread;
import ch.awae.moba.core.util.JarScanner;
import ch.awae.moba.core.util.Pair;
import ch.awae.moba.core.util.Registries;

/**
 * Manages (almost) everything
 */
public final class Core {

    public static Core init() throws IOException {
        return new Core();
    }

    private final SPIThread spi;
    private final Model     model;

    private Core() throws IOException {
        this.spi = new SPIThread();
        this.model = new Model();
    }

    public Model getModel() {
        return this.model;
    }

    @SuppressWarnings("unused")
    public void registerHost(SPIChannel channel, Sector sector, String title) {
        Pair<SPIHost, Host> host = HostFactory.createHost(channel, title);
        new ProcessorThread(host._2, new AdditiveProcessor(this.model, sector));
        this.spi.registerHost(host._1);
    }

    public void loadOperators(String pkg) throws IOException {
        String jar = System.getProperty("path");
        if (jar == null)
            throw new IllegalArgumentException("System Property 'path' required!");
        JarScanner.loadOperators(jar, pkg);
    }

    public void startConsole() {
        new ConsoleThread(this.model).start();
    }

    @SuppressWarnings("unused")
    public void start() throws InterruptedException {
        new OperatorThread(this.model);
        this.spi.start();
        Thread.sleep(2000);
        for (String name : Registries.threads.getNames()) {
            try {
                IThreaded thread = Registries.threads.get(name);
                if (thread != null && !thread.isActive())
                    thread.start();
            } catch (RuntimeException e) {
                System.out.println(e);
            }
        }
    }

    public void startOperators() {
        for (String name : Registries.operators.getNames()) {
            AOperator op = Registries.operators.get(name);
            if (op != null && op.isEnabled())
                op.start();
        }
    }

}
