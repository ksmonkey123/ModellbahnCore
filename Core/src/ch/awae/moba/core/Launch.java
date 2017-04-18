package ch.awae.moba.core;

import static ch.awae.moba.core.model.Sector.*;
import static ch.awae.moba.core.spi.SPIChannel.*;

import java.io.IOException;

public class Launch {

    public static void main(String[] args)
            throws InterruptedException, IllegalAccessException, IOException {
        Core c = Core.init();
        c.registerHost(CH_0, BOTTOM, "bottom");
        c.registerHost(CH_1, LEFT, "left");
        c.registerHost(CH_2, RIGHT, "right");
        c.registerHost(CH_3, CENTER, "center");
        c.loadOperators();
        c.startConsole();
        c.start();
        c.startOperators();
    }

}
