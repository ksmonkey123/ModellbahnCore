package ch.awae.moba.core;

import java.io.IOException;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.Operators;
import ch.awae.moba.core.processors.BottomProcessor;
import ch.awae.moba.core.processors.CenterProcessor;
import ch.awae.moba.core.processors.LeftProcessor;
import ch.awae.moba.core.processors.RightProcessor;
import ch.awae.moba.core.spi.HostFactory;
import ch.awae.moba.core.spi.SPIChannel;
import ch.awae.moba.core.threads.ConsoleThread;
import ch.awae.moba.core.threads.IThreaded;
import ch.awae.moba.core.threads.OperatorThread;
import ch.awae.moba.core.threads.SPIThread;
import ch.awae.moba.core.util.Registries;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException {
		Model model = new Model();
		SPIThread spi_thread = new SPIThread();

		new OperatorThread(model);

		Operators.getCount();

		HostFactory.createHost(SPIChannel.CH_0, spi_thread, new BottomProcessor(model), "Basement Controller");
		HostFactory.createHost(SPIChannel.CH_1, spi_thread, new LeftProcessor(model), "Left Controller");
		HostFactory.createHost(SPIChannel.CH_2, spi_thread, new RightProcessor(model), "Right Controller");
		HostFactory.createHost(SPIChannel.CH_3, spi_thread, new CenterProcessor(model), "Center Controller");

		new ConsoleThread(model).start();

		spi_thread.start();

		Thread.sleep(2000);

		for (String name : Registries.threads.getNames()) {
			try {
				IThreaded thread = Registries.threads.get(name);
				if (thread != null)
					thread.start();
			} catch (RuntimeException e) {
				System.out.println(e);
			}
		}

	}
}
