package ch.awae.moba.core.logic;

import java.util.Objects;

import ch.awae.moba.core.model.Model;

/**
 * Inverted {@link Logic} instance
 * 
 * @author Andreas Wälchli
 * 
 * @see Logic
 */
final class NotLogic implements Logic {

    private final Logic λ;

    /**
     * Creates a new NotLogic instance from a backing Logic instance
     * 
     * @param backer
     *            the backing instance. May not be {@code null}
     * @throws NullPointerException
     *             if {@code backer} is {@code null}
     */
    public NotLogic(Logic backer) {
        λ = Objects.requireNonNull(backer, "backer may not be null");
    }

    @Override
    public boolean evaluate(Model m) {
        return !λ.evaluate(m);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Returns the backer itself, such that the following performance
     * optimisation is provided:<br/>
     * {@code new NotLogic(a).not() == a}
     * </p>
     */
    @Override
    public Logic not() {
        return λ;
    }

}
