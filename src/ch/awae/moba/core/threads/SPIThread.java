package ch.awae.moba.core.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Logger;

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

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.spi.SPIChannel;
import ch.awae.moba.core.spi.SPIHost;
import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.Utils;

public class SPIThread implements IThreaded {

    final long HOST_SELECT_DELAY;
    final int  SPI_SPEED;
    final byte MAGIC_NUMBER;

    final HashMap<SPIChannel, GpioPinDigitalOutput> pinMap;

    final Logger         logger = Utils.getLogger();
    final List<SPIHost>  hosts;
    final SpiDevice      spi;
    final GpioController gpio;

    private volatile Thread thread = null;

    public synchronized void registerHost(SPIHost host) {
        SPIChannel c = host.getChannel();
        if (this.pinMap.containsKey(c)) {
            this.logger.severe("already registered a device on channel " + c);
            throw new IllegalArgumentException("occupied channel " + c);
        }
        GpioPinDigitalOutput pin = this.gpio.provisionDigitalOutputPin(c.pin, PinState.LOW);
        this.logger.info("loaded device " + host.getName() + " on channel " + host.getChannel());
        this.pinMap.put(c, pin);
        this.hosts.add(host);
        pin.setShutdownOptions(Boolean.TRUE, PinState.LOW, PinPullResistance.OFF,
                PinMode.DIGITAL_INPUT);
    }

    public SPIThread() throws IOException {
        this.hosts = new ArrayList<>();
        this.pinMap = new HashMap<>();

        Properties props = Configs.getProperties("spi");

        this.SPI_SPEED = Integer.parseInt(props.getProperty("spi.speed"), 10);
        this.HOST_SELECT_DELAY = Long.parseLong(props.getProperty("spi.hostSelectDelay"), 10);
        this.MAGIC_NUMBER = Byte.parseByte(props.getProperty("spi.magicNumber"), 10);

        GpioController controller = GpioFactory.getInstance();
        SpiDevice device = SpiFactory.getInstance(SpiChannel.CS0, this.SPI_SPEED, SpiMode.MODE_0);

        assert device != null;
        assert controller != null;

        this.spi = device;
        this.gpio = controller;

        Registries.threads.register("spi", this);
    }

    @Override
    public synchronized void start() {
        if (this.thread != null)
            throw new IllegalStateException("already running");
        this.logger.info("starting thread");
        Thread t = new SPILoop();
        t.start();
        this.thread = t;
    }

    @Override
    public synchronized void stop() throws InterruptedException {
        Thread t = this.thread;
        if (t == null)
            throw new IllegalStateException("already halted");
        this.logger.info("stopping thread");
        t.interrupt();
        t.join();
        this.thread = null;
        this.logger.info("thread stopped");
    }

    @Override
    public boolean isActive() {
        return this.thread != null;
    }

    private class SPILoop extends Thread {

        SPILoop() {
            super();
        }

        @Override
        public void run() {
            loop: while (!this.isInterrupted()) {
                list: for (int i = 0; i < SPIThread.this.hosts.size(); i++) {
                    SPIHost host = SPIThread.this.hosts.get(i);
                    GpioPinDigitalOutput pin = SPIThread.this.pinMap.get(host.getChannel());
                    assert pin != null;
                    pin.setState(PinState.HIGH);
                    if (SPIThread.this.HOST_SELECT_DELAY > 0)
                        LockSupport.parkNanos(SPIThread.this.HOST_SELECT_DELAY);

                    short input = host.getInput();
                    byte[] array = { SPIThread.this.MAGIC_NUMBER, (byte) (input & 0x00ff),
                            (byte) ((input >> 8) & 0x00ff), host.getNetwork() };
                    byte[] response;
                    try {
                        response = SPIThread.this.spi.write(array);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    pin.setState(PinState.LOW);
                    assert response != null;
                    if (response[0] != SPIThread.this.MAGIC_NUMBER) {
                        if (response[0] != -1)
                            SPIThread.this.logger.fine("Invalid response from device "
                                    + host.getName() + " on channel " + host.getChannel()
                                    + "\n > magic number was wrong: " + response[0]);
                        continue list;
                    }
                    byte check = (byte) ((array[1] ^ array[2]) & 0x000000ff);
                    if (response[3] != check) {
                        SPIThread.this.logger.fine("Invalid response from device " + host.getName()
                                + " on channel " + host.getChannel() + "\n > invalid readback: "
                                + response[3] + " instead of " + check);
                        continue list;
                    }
                    short output = (short) (((response[2] << 8) & 0x0000ff00)
                            | (response[1] & 0x000000ff));
                    host.setOutput(output);

                    if (this.isInterrupted())
                        break loop;
                }
            }
        }

    }

}
