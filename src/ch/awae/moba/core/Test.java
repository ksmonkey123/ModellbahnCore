package ch.awae.moba.core;

import java.io.IOException;

import ch.awae.moba.core.spi.HostFactory;
import ch.awae.moba.core.spi.SPIChannel;
import ch.awae.moba.core.spi.SPIGroup;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		SPIGroup group = new SPIGroup();

		HostFactory.createHost(SPIChannel.CH_0, group);
		HostFactory.createHost(SPIChannel.CH_1, group);
		HostFactory.createHost(SPIChannel.CH_2, group);
		HostFactory.createHost(SPIChannel.CH_3, group);

		SPIThread spi_thread = new SPIThread(group);

		spi_thread.start();

		Thread.sleep(60000);

		spi_thread.stop();

	}
}
