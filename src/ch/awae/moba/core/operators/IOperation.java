package ch.awae.moba.core.operators;

/**
 * Base interface for all operators
 * 
 * @author Andreas Wälchli
 * @see Operator
 */
public interface IOperation {

    /**
     * Triggers a <em>single</em> update of the operator
     */
    public void update();

    /**
     * Called whenever the operator is being started
     */
    default void onStart() {
        // default no-op
    }

    /**
     * Called whenever the operator is being halted
     */
    default void onStop() {
        // default no-op
    }

}
