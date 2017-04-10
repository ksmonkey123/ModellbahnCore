package ch.awae.moba.core.operators;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.util.Controllable;
import ch.awae.moba.core.util.Registries;

public abstract class IOperator implements Controllable {

	public IOperator(String title) {
		this.title = title;
		Registries.operators.register(title, this);
	}

	public final String title;

	public abstract void update(Model model);

	private boolean state = true;

	public final void setState(boolean state) {
		this.state = state;
	}

	public boolean isActive() {
		return this.state;
	}

	@Override
	public void start() {
		if (state)
			throw new IllegalStateException("already running");
		state = true;
	}

	@Override
	public void halt() {
		if (!state)
			throw new IllegalStateException("already halted");
		state = false;
	}

}
