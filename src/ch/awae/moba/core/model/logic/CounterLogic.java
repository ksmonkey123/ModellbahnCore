package ch.awae.moba.core.model.logic;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.Model;

public class CounterLogic implements Logic {

	private Logic[] backers;
	private int target;

	public CounterLogic(int target, Logic... backers) {
		this.target = target;
		this.backers = backers;
	}

	@Override
	public boolean evaluate(@NonNull Model m) {
		int acc = 0;
		for (Logic l : backers)
			if (l.evaluate(m))
				acc++;
		return acc == target;
	}

}
