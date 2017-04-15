package ch.awae.moba.core.operators.impl;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.AOperator;

public class LeftClearOperator extends AOperator {

	public LeftClearOperator() {
		super("left (clear)");
	}

	@Override
	public void update(@NonNull Model model) {
		if (ButtonMapping.L_CLEAR.evaluate(model))
			model.paths.register(Path.L_CLEAR);
	}

}
