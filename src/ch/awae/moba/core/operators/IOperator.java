package ch.awae.moba.core.operators;

import ch.awae.moba.core.util.Controllable;

public interface IOperator extends Controllable {

    String getName();

    void update();

}
