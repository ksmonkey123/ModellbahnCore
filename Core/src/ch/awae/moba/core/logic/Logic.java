package ch.awae.moba.core.logic;

import ch.awae.moba.core.model.Model;

/**
 * Base interface for composeable boolean logic operating on a {@link Model}.
 * 
 * <p>
 * This interface can be implemented by any type capable of evaluating the
 * current {@link Model} state to a boolean value. This interface also
 * implements composition functions.
 * </p>
 * 
 * @author Andreas Wälchli
 * @see NotLogic
 * @see AndLogic
 * @see OrLogic
 * @see CounterLogic
 */
@FunctionalInterface
public interface Logic {

    /**
     * Evaluates the given {@link Model} to a boolean value. Usually checks if
     * the current Model state satisfies a defined condition.
     * <p>
     * It is recommended that all implementations reject {@code null} Models
     * with a {@link NullPointerException}. The predefined composed Logic
     * instances do not perform {@code null}-checks and pass the Model to their
     * backer objects directly.
     * </p>
     * 
     * @param m
     *            the {@link Model} to evaluate
     * @return a boolean evaluation of the model
     */
    boolean evaluate(Model m);

    /**
     * Inverts the Logic instance
     * 
     * @return an inverted Logic instance
     * @see NotLogic
     */
    default Logic not() {
        return new NotLogic(this);
    }

    /**
     * Composes this instance with an other one with the {@code AND}-operator
     * 
     * @param l
     *            the instance to compose this one with. In the default
     *            implementation this may not be {@code null}
     * @return the {@code AND}-Composed instance
     * @throws NullPointerException
     *             in the default implementation if {@code l} is {@code null}
     * @see AndLogic
     */
    default Logic and(Logic l) {
        return new AndLogic(this, l);
    }

    /**
     * Composes this instance with an other one with the {@code IOR}-operator
     * 
     * @param l
     *            the instance to compose this one with. In the default
     *            implementation this may not be {@code null}
     * @return the {@code IOR}-Composed instance
     * @throws NullPointerException
     *             in the default implementation if {@code l} is {@code null}
     * @see OrLogic
     */
    default Logic or(Logic l) {
        return new OrLogic(this, l);
    }

    /**
     * Factory method for a counting Logic instance. This evaluates to
     * {@code true} if the number of Logic instances that evaluate to
     * {@code true} matches the parameter {@code target}.
     * 
     * @param target
     *            the target number of satisfied Logic instances. Must be a
     *            number between zero and {@code logics.length} (both
     *            inclusive).
     * @param logics
     *            the Logic instances to check. May not be {@code null} and may
     *            not contain {@code null} elements.
     * @return a counting Logic instance
     * @throws NullPointerException
     *             if {@code logics} is {@code null} or contains
     *             {@code null} elements.
     * @throws IllegalArgumentException
     *             if {@code target} is not in the inclusive range
     *             {@code [0, logics.length]}
     * @see CounterLogic
     */
    static Logic count(int target, Logic... logics) {
        return new CounterLogic(target, logics);
    }

}
