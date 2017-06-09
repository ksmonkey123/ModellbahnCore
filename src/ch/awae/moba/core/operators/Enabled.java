package ch.awae.moba.core.operators;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates whether an operator is enabled or not.
 * 
 * Enabled operators are activated by default during startup.
 * 
 * <p>
 * This annotation defaults to {@code enabled}; therefore {@code @Enabled} is
 * equivalent to {@code @Enabled(true)}. To disable an operator annotate it with
 * {@code @Enabled(false)}.
 * </p>
 * 
 * @author Andreas Wälchli
 * @see IOperation
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Enabled {

    boolean value() default true;

}
