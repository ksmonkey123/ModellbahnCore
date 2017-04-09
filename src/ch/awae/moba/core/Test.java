package ch.awae.moba.core;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.CenterBaseOperator;
import ch.awae.moba.core.processors.CenterProcessor;
import ch.awae.moba.core.processors.NullProcessor;
import ch.awae.moba.core.spi.HostFactory;
import ch.awae.moba.core.spi.SPIChannel;
import ch.awae.moba.core.threads.ConsoleThread;
import ch.awae.moba.core.threads.IThreaded;
import ch.awae.moba.core.threads.OperatorThread;
import ch.awae.moba.core.threads.SPIThread;
import ch.awae.moba.core.util.ThreadRegistry;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		Model model = new Model();
		SPIThread spi_thread = new SPIThread();
		OperatorThread operator = new OperatorThread(model);

		NullProcessor nullProcessor = new NullProcessor(model);

		HostFactory.createHost(SPIChannel.CH_0, spi_thread, nullProcessor, "Basement Controller");
		HostFactory.createHost(SPIChannel.CH_1, spi_thread, nullProcessor, "Left Controller");
		HostFactory.createHost(SPIChannel.CH_2, spi_thread, nullProcessor, "Right Controller");
		HostFactory.createHost(SPIChannel.CH_3, spi_thread, new CenterProcessor(model), "Center Controller");

		operator.registerOperator(new CenterBaseOperator());

		new ConsoleThread().start();

		for (String name : ThreadRegistry.getNames()) {
			IThreaded thread = ThreadRegistry.getThread(name);
			if (thread != null)
				thread.start();
		}
		
		Logger.getGlobal().setLevel(Level.SEVERE);

	}
}
