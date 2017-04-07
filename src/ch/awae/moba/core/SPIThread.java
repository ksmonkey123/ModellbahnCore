package ch.awae.moba.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;

import ch.awae.moba.core.spi.SPIChannel;
import ch.awae.moba.core.spi.SPIGroup;
import ch.awae.moba.core.spi.SPIHost;

public class SPIThread {

	private static final long HOST_SELECT_DELAY = 100000;
	private static final int SPI_SPEED = 61000;
	private static final byte MAGIC_NUMBER = 0b01101001;

	private final Logger logger;
	private final HashMap<SPIChannel, @Nullable GpioPinDigitalOutput> pinMap;
	private final List<SPIHost> hosts;
	private final SpiDevice spi;

	private volatile @Nullable Thread thread = null;

	public SPIThread(SPIGroup group) throws IOException {
		Logger logger = Logger.getLogger("SPIThread");
		assert logger != null;
		this.logger = logger;
		this.hosts = new ArrayList<>();
		this.pinMap = new HashMap<>();

		SpiDevice spi = SpiFactory.getInstance(SpiChannel.CS0, SPI_SPEED, SpiMode.MODE_0);
		GpioController gpio = GpioFactory.getInstance();

		assert spi != null;
		assert gpio != null;

		this.spi = spi;

		for (SPIHost host : group.getHostList()) {
			SPIChannel c = host.getChannel();
			GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(c.pin, PinState.LOW);
			logger.info("loaded " + pin.getPin().getAddress());
			pinMap.put(c, pin);
			hosts.add(host);
			pin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF, PinMode.DIGITAL_INPUT);
		}
	}

	public synchronized void start() {
		if (this.thread != null)
			throw new IllegalStateException("already running");
		Thread t = new SPILoop();
		t.start();
		this.thread = t;
	}

	public synchronized void stop() {
		Thread t = this.thread;
		if (t == null)
			throw new IllegalStateException("already halted");
		t.interrupt();
		this.thread = null;
	}

	private class SPILoop extends Thread {

		@Override
		public void run() {
			loop: while (!this.isInterrupted()) {
				list: for (SPIHost host : hosts) {
					GpioPinDigitalOutput pin = pinMap.get(host.getChannel());
					assert pin != null;
					pin.setState(PinState.HIGH);
					if (HOST_SELECT_DELAY > 0)
						LockSupport.parkNanos(HOST_SELECT_DELAY);

					short input = host.getInput();
					byte[] array = { MAGIC_NUMBER, (byte) ((input >> 8) & 0x00ff), (byte) (input & 0x00ff),
							host.getNetwork() };
					byte[] response;
					try {
						response = spi.write(array);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					pin.setState(PinState.LOW);
					assert response != null;
					if (response[0] != MAGIC_NUMBER) {
						logger.warning("Invalid response from device on channel " + host.getChannel()
								+ "\n > magic number was wrong: " + response[0]);
						continue list;
					}
					byte check = (byte) ((array[1] ^ array[2]) & 0x000000ff);
					if (response[3] != check) {
						logger.warning("Invalid response from device on channel " + host.getChannel()
								+ "\n > invalid readback: " + response[3] + " instead of " + check);
						continue list;
					}
					short output = (short) (((response[1] << 8) & 0x0000ff00) | (response[2] & 0x000000ff));
					host.setOutput(output);

					if (this.isInterrupted())
						break loop;
				}
			}
		}

	}

}