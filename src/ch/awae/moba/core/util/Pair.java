package ch.awae.moba.core.util;

public final class Pair<A, B> {

    public final A _1;
    public final B _2;

    public Pair(A _1, B _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public static <A, B> Pair<A, B> of(A _1, B _2) {
        return new Pair<>(_1, _2);
    }

}
