package ch.awae.moba.core.util;

/**
 * Denotes any element that can be activated and deactivated.
 * <p>
 * On creation an element should generally be halted, but in some case
 * start-on-load behaviour may be viable.
 * </p>
 * 
 * @author Andreas Wälchli
 */
public interface Controllable {

    /**
     * Starts the instance.
     * 
     * If this instance is already running, ignore this call.
     */
    void start();

    /**
     * Halts the instance.
     * 
     * If this instance is already halted, ignore this call.
     */
    void halt();

    /**
     * Indicates whether or not this instance is currently started.
     * 
     * @return {@code true} iff the this instance is currently started.
     */
    boolean isActive();

}
