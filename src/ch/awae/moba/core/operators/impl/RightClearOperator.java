package ch.awae.moba.core.operators.impl;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.IOperator;

public class RightClearOperator extends IOperator {

	public RightClearOperator() {
		super("right (clear)");
	}

	@Override
	public void update(@NonNull Model model) {
		if (ButtonMapping.R_CLR_A.evaluate(model))
			model.paths.register(Path.R_CLR_A);
		if (ButtonMapping.R_CLR_B.evaluate(model))
			model.paths.register(Path.R_CLR_B);
	}

}
