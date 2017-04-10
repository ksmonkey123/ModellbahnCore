package ch.awae.moba.core.model.logic;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;

public class ButtonLogic implements Logic {

	private final ButtonMapping mapping;
	private final boolean state;

	public ButtonLogic(ButtonMapping mapping, boolean high) {
		this.mapping = mapping;
		this.state = high;
	}

	@Override
	public boolean evaluate(@NonNull Model m) {
		return m.buttons.getState(mapping) == state;
	}

}
