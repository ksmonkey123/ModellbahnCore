package ch.awae.moba.core.operators.impl;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.IOperator;

public class CenterBaseOperator extends IOperator {

	public CenterBaseOperator() {
		super("center base");
	}

	private final ButtonMapping[] BUTTONS = { ButtonMapping.C_S_1_A, ButtonMapping.C_S_1_B, ButtonMapping.C_S_2_A,
			ButtonMapping.C_S_2_B, ButtonMapping.C_S_3_A, ButtonMapping.C_S_3_B, ButtonMapping.C_S_4_A,
			ButtonMapping.C_S_4_B };
	private final Path[] PATHS = { Path.C_S_1_A, Path.C_S_1_B, Path.C_S_2_A, Path.C_S_2_B, Path.C_S_3_A, Path.C_S_3_B,
			Path.C_S_4_A, Path.C_S_4_B };

	@SuppressWarnings("null")
	@Override
	public void update(Model model) {

		if (model.buttons.getState(ButtonMapping.C_CLEAR)) {
			model.paths.register(Path.C_CLEAR);
			return;
		}

		for (int i = 0; i < BUTTONS.length; i++) {
			if (model.buttons.getState(BUTTONS[i]))
				model.paths.register(PATHS[i]);
		}

	}

}
