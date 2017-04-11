package ch.awae.moba.core.model.logic;

import ch.awae.moba.core.model.Model;

@FunctionalInterface
public interface Logic {

	boolean evaluate(Model m);

	default Logic not() {
		return new NotLogic(this);
	}

	default Logic and(Logic l) {
		return new AndLogic(this, l);
	}

	default Logic or(Logic l) {
		return new OrLogic(this, l);
	}

	static Logic count(int target, Logic... logics) {
		return new CounterLogic(target, logics);
	}

}
