package ch.awae.moba.core.spi;

public interface SPIHost {

	/**
	 * provides the input for the SPI device
	 * 
	 * @return the 16-bit input
	 */
	short getInput();

	/**
	 * provides the network value for the SPI device
	 * 
	 * @return the 8-bit network value
	 */
	byte getNetwork();

	/**
	 * sets the value received from the SPI device
	 * 
	 * @param output
	 *            the 16-bit value received from the device
	 */
	void setOutput(short output);

	/**
	 * provides the SPI channel to use for transmission
	 * 
	 * @return the channel
	 */
	SPIChannel getChannel();
	
	String getName();
}
