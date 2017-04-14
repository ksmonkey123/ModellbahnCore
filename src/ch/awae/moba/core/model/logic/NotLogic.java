package ch.awae.moba.core.model.logic;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.Model;

public class NotLogic implements Logic {

	private final Logic backer;

	public NotLogic(Logic backer) {
		this.backer = backer;
	}

	@Override
	public boolean evaluate(@NonNull Model m) {
		return !this.backer.evaluate(m);
	}

	@Override
	public @NonNull Logic not() {
		return this.backer;
	}

}
