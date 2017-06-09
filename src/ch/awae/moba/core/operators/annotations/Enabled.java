package ch.awae.moba.core.operators.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Enabled {

	boolean value() default true;

}
