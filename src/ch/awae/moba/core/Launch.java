package ch.awae.moba.core;

import java.io.IOException;

public class Launch {

    public static void main(String[] args)
            throws InterruptedException, IllegalAccessException, IOException {
        Core c = Core.init();
        c.loadHosts();
        c.loadOperators();
        c.startConsole();
        c.start();
        c.startOperators();
    }

}
