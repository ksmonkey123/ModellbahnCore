package ch.awae.moba.core.operators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import ch.awae.moba.core.model.Model;

/**
 * Marks an instance field in an operator, that shall have a value injected
 * during loading. Currently this is only applicable to fields with the type
 * {@link Model}. A Model instance is provided during loading.
 * 
 * @author Andreas Wälchli
 * @see Model
 * @see IOperation
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface External {
    // no fields
}
