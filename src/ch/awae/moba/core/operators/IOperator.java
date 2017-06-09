package ch.awae.moba.core.operators;

import ch.awae.moba.core.util.Controllable;

/**
 * Internal base interface for all operators. This should not be implemented
 * externally. Use the {@link IOperation} interface instead.
 * 
 * <p>
 * <b>This is for internal use only!</b>
 * </p>
 * 
 * @author Andreas Wälchli
 * @see IOperation
 */
public interface IOperator extends Controllable {

    String getName();

    void update();

}
