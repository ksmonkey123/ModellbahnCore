package ch.awae.moba.core;

import static ch.awae.moba.core.model.Sector.BOTTOM;
import static ch.awae.moba.core.model.Sector.CENTER;
import static ch.awae.moba.core.model.Sector.LEFT;
import static ch.awae.moba.core.model.Sector.RIGHT;
import static ch.awae.moba.core.spi.SPIChannel.CH_0;
import static ch.awae.moba.core.spi.SPIChannel.CH_1;
import static ch.awae.moba.core.spi.SPIChannel.CH_2;
import static ch.awae.moba.core.spi.SPIChannel.CH_3;

import java.io.IOException;

import ch.awae.moba.core.util.OperatorLoader;

public class Launch {

    public static void main(String[] args)
            throws IOException, InterruptedException, IllegalAccessException, RuntimeException {
        Core c = Core.init();

        OperatorLoader.loadOperators(c.getModel());

        System.exit(-1);

        c.registerHost(CH_0, BOTTOM, "bottom");
        c.registerHost(CH_1, LEFT, "left");
        c.registerHost(CH_2, RIGHT, "right");
        c.registerHost(CH_3, CENTER, "center");
        c.loadOperators("ch.awae.moba.core.operators.impl");
        c.startConsole();
        c.start();
        c.startOperators();
    }

}
