package ch.awae.moba.core.logic;

import java.util.Objects;

import ch.awae.moba.core.model.Model;

/**
 * {@code Logic} instance representing an {@code AND}-composition of two logic
 * instances.
 * 
 * @author Andreas Wälchli
 * @see Logic
 */
public class AndLogic implements Logic {

    private final Logic a, b;

    /**
     * Creates a new instance
     * 
     * @param a
     *            the first operand. May not be {@code null}
     * @param b
     *            the second operand. May not be {@code null}
     * @throws NullPointerException
     *             if {@code a} or {@code b} is {@code null}
     */
    public AndLogic(Logic a, Logic b) {
        this.a = Objects.requireNonNull(a, "a may not be null");
        this.b = Objects.requireNonNull(b, "b may not be null");
    }

    @Override
    public boolean evaluate(Model m) {
        return this.a.evaluate(m) && this.b.evaluate(m);
    }

}
