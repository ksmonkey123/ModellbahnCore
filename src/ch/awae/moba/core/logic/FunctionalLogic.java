package ch.awae.moba.core.logic;

import java.util.Objects;
import java.util.function.Function;

import ch.awae.moba.core.model.Model;

/**
 * Functional implementation of the {@link Logic} interface. Allows the
 * evaluation to be determined by an externally provided function
 * 
 * @author Andreas Wälchli
 * 
 * @see Logic
 */
final class FunctionalLogic implements Logic {

    private final Function<Model, Boolean> λ;

    /**
     * Creates a new instance.
     * 
     * @param λάμδα
     *            the function to be evaluated for the logic value
     * @throws NullPointerException
     *             if {@code λάμδα} is {@code null}
     */
    FunctionalLogic(final Function<Model, Boolean> λάμδα) {
        λ = Objects.requireNonNull(λάμδα, "'λάμδα' may not be null!");
    }

    @Override
    public boolean evaluate(Model model) {
        return λ.apply(model);
    }

}
