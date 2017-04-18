package ch.awae.moba.core.spi;

import org.eclipse.jdt.annotation.Nullable;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Set of all legal SPI host channels
 */
public enum SPIChannel {

	CH_0(RaspiPin.GPIO_00), CH_1(RaspiPin.GPIO_01), CH_2(RaspiPin.GPIO_02), CH_3(RaspiPin.GPIO_03), CH_4(
			RaspiPin.GPIO_04);

	public final Pin pin;

	SPIChannel(final @Nullable Pin pin) {
		assert pin != null;
		this.pin = pin;
	}

}
