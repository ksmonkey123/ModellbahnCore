package ch.awae.moba.core.spi;

import ch.awae.moba.core.model.Sector;

public interface Host {

    /**
     * enqueues the given values for transmission in the next write cycle.
     * 
     * @param data
     *            the 16-bit data field
     * @param network
     *            the 8-bit network value
     */
    void write(short data, byte network);

    /**
     * provides the data read from the device during the last read cycle.
     * 
     * @return the 16-bit data field
     * 
     * @see #isUpdated()
     */
    short read();

    /**
     * indicates if a read/write cycle has occurred since the last call to
     * {@link #read()}
     * 
     * @return {@code true} if a read/write cycle has occurred
     */
    @Deprecated
    boolean isUpdated();

    String getName();
    
    Sector getSector();

}
