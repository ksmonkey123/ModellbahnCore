package ch.awae.moba.core.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class Lazy<A> {

    private A                value     = null;
    private RuntimeException exception = null;

    private boolean     set = false;
    private Supplier<A> f;

    public Lazy(Supplier<A> f) {
        this.f = f;
    }

    private Lazy(A value, RuntimeException exception) {
        this.value = value;
        this.exception = exception;
        set = true;
        f = null;
    }

    public static <A> Lazy<A> completed(A state) {
        return new Lazy<>(state, null);
    }

    public static <A> Lazy<A> failed(RuntimeException exception) {
        return new Lazy<>(null, exception);
    }

    public A get() {
        if (!set)
            setIfUnset();
        if (exception != null)
            throw exception;
        return value;
    }

    public RuntimeException getException() {
        return exception;
    }

    boolean isCompleted() {
        return set;
    }

    boolean failed() {
        return exception != null;
    }

    private synchronized void setIfUnset() {
        if (set)
            return;
        try {
            value = f.get();
        } catch (RuntimeException rex) {
            exception = rex;
            throw rex;
        } finally {
            f = null;

            set = true;
        }
    }

    public <B> Lazy<B> map(Function<A, B> mapping) {
        return new Lazy<>(() -> mapping.apply(get()));
    }

}
