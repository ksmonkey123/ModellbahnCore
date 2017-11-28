package ch.awae.moba.core.operators;

public interface IStatefulOperation<State> extends IOperation {

    State saveState();

    void loadState(State state);

}
