package ch.awae.moba.core.threads;

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
import ch.awae.moba.core.spi.SPIHost;
import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.Utils;

public class SPIThread implements IThreaded {

	private static final long HOST_SELECT_DELAY = 150000;
	private static final int SPI_SPEED = 61000;
	private static final byte MAGIC_NUMBER = 0b01101001;

	private final Logger logger = Utils.getLogger();
	private final HashMap<SPIChannel, @Nullable GpioPinDigitalOutput> pinMap;
	private final List<SPIHost> hosts;
	private final SpiDevice spi;
	private final GpioController gpio;

	private volatile @Nullable Thread thread = null;

	public synchronized void registerHost(SPIHost host) {
		SPIChannel c = host.getChannel();
		if (pinMap.containsKey(c)) {
			logger.severe("already registered a device on channel " + c);
			throw new IllegalArgumentException("occupied channel " + c);
		}
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(c.pin, PinState.LOW);
		logger.info("loaded device " + host.getName() + " on channel " + host.getChannel());
		pinMap.put(c, pin);
		hosts.add(host);
		pin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF, PinMode.DIGITAL_INPUT);
	}

	public SPIThread() throws IOException {
		this.hosts = new ArrayList<>();
		this.pinMap = new HashMap<>();

		GpioController gpio = GpioFactory.getInstance();

		SpiDevice spi = SpiFactory.getInstance(SpiChannel.CS0, SPI_SPEED, SpiMode.MODE_0);

		assert spi != null;
		assert gpio != null;

		this.spi = spi;
		this.gpio = gpio;

		Registries.threads.register("spi", this);
	}

	public synchronized void start() {
		if (this.thread != null)
			throw new IllegalStateException("already running");
		logger.info("starting thread");
		Thread t = new SPILoop();
		t.start();
		this.thread = t;
	}

	public synchronized void stop() throws InterruptedException {
		Thread t = this.thread;
		if (t == null)
			throw new IllegalStateException("already halted");
		logger.info("stopping thread");
		t.interrupt();
		t.join();
		this.thread = null;
		logger.info("thread stopped");
	}

	@Override
	public boolean isActive() {
		return thread != null;
	}

	private class SPILoop extends Thread {

		@Override
		public void run() {
			loop: while (!this.isInterrupted()) {
				list: for (int i = 0; i < hosts.size(); i++) {
					@SuppressWarnings("null")
					SPIHost host = hosts.get(i);
					GpioPinDigitalOutput pin = pinMap.get(host.getChannel());
					assert pin != null;
					pin.setState(PinState.HIGH);
					if (HOST_SELECT_DELAY > 0)
						LockSupport.parkNanos(HOST_SELECT_DELAY);

					short input = host.getInput();
					byte[] array = { MAGIC_NUMBER, (byte) (input & 0x00ff), (byte) ((input >> 8) & 0x00ff),
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
						if (response[0] != -1)
							logger.warning("Invalid response from device " + host.getName() + " on channel "
									+ host.getChannel() + "\n > magic number was wrong: " + response[0]);
						continue list;
					}
					byte check = (byte) ((array[1] ^ array[2]) & 0x000000ff);
					if (response[3] != check) {
						logger.warning("Invalid response from device " + host.getName() + " on channel "
								+ host.getChannel() + "\n > invalid readback: " + response[3] + " instead of " + check);
						continue list;
					}
					short output = (short) (((response[2] << 8) & 0x0000ff00) | (response[1] & 0x000000ff));
					host.setOutput(output);

					if (this.isInterrupted())
						break loop;
				}
			}
		}

	}

}