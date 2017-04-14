package ch.awae.moba.core.operators;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.util.Controllable;
import ch.awae.moba.core.util.Registries;

public abstract class IOperator implements Controllable {

	public IOperator(String title) {
		this.title = title;
		Registries.operators.register(title, this);
	}

	public final String title;

	public abstract void update(Model model);

	private boolean state = false;

	public final void setState(boolean state) {
		this.state = state;
	}

	@Override
    public boolean isActive() {
		return this.state;
	}

	@Override
	public void start() {
		if (this.state)
			throw new IllegalStateException("already running");
		this.state = true;
	}

	@Override
	public void halt() {
		if (!this.state)
			throw new IllegalStateException("already halted");
		this.state = false;
	}

	public boolean isEnabled() {
		@Nullable
		Enabled field = this.getClass().getAnnotation(Enabled.class);
		if (field != null && !field.value())
			return false;
		return true;
	}

}
