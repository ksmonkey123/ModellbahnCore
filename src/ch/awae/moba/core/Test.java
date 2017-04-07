package ch.awae.moba.core;

import java.io.IOException;

import ch.awae.moba.core.spi.Host;
import ch.awae.moba.core.spi.HostFactory;
import ch.awae.moba.core.spi.SPIChannel;
import ch.awae.moba.core.spi.SPIGroup;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		SPIGroup group = new SPIGroup();

		Host a = HostFactory.createHost(SPIChannel.CH_0, group);
		Host b = HostFactory.createHost(SPIChannel.CH_1, group);
		Host c = HostFactory.createHost(SPIChannel.CH_2, group);
		Host d = HostFactory.createHost(SPIChannel.CH_3, group);

		b.write((short) 0xf00f, (byte) 0x00);
		c.write((short) 0xf00f, (byte) 0xff);

		SPIThread spi_thread = new SPIThread(group);

		spi_thread.start();

		Thread.sleep(60000);

		spi_thread.stop();

	}
}
