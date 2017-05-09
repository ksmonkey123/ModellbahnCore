package ch.awae.moba.core.logic;

import java.util.Objects;

import ch.awae.moba.core.model.Model;

/**
 * A CounterLogic evaluates a set of other {@code Logic} instances.
 * 
 * It evaluates to true if the number of {@code true}-evaluating Logic instances
 * matches a given fixed number exactly.
 * 
 * @author Andreas Wälchli
 * @see Logic
 */
public class CounterLogic implements Logic {

    private Logic[] backers;
    private int     target;

    /**
     * Creates a new counting Logic instance
     * 
     * @param target
     *            the target number of satisfied Logic instances. Must be a
     *            number between zero and {@code logics.length} (both
     *            inclusive).
     * @param backers
     *            the Logic instances to check. May not be {@code null} and may
     *            not contain {@code null} elements.
     * @throws NullPointerException
     *             if {@code backers} is {@code null} or contains
     *             {@code null} elements.
     * @throws IllegalArgumentException
     *             if {@code target} is not in the inclusive range
     *             {@code [0, backers.length]}
     * @see CounterLogic
     */
    public CounterLogic(int target, Logic... backers) {
        this.backers = Objects.requireNonNull(backers, "backers may not be null");
        if (target < 0 || target > backers.length)
            throw new IllegalArgumentException(
                    "target (" + target + ") must lie in the range [0," + backers.length + "]");
        this.target = target;
        for (int i = 0; i < backers.length; i++) {
            Objects.requireNonNull(backers[i], "backers[" + i + "] may not be null");
        }
    }

    @Override
    public boolean evaluate(Model m) {
        int acc = 0;
        for (Logic l : this.backers)
            if (l.evaluate(m))
                acc++;
        return acc == this.target;
    }

}
