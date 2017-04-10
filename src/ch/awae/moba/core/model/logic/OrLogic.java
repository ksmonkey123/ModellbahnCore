package ch.awae.moba.core.model.logic;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.Model;

public class OrLogic implements Logic {

	private final Logic a, b;

	public OrLogic(Logic a, Logic b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean evaluate(@NonNull Model m) {
		return a.evaluate(m) || b.evaluate(m);
	}

}
