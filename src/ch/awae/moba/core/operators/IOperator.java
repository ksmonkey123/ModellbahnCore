package ch.awae.moba.core.operators;

import ch.awae.moba.core.model.Model;

@FunctionalInterface
public interface IOperator {

	void update(Model model);

}
