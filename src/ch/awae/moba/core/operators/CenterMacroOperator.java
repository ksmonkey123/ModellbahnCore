package ch.awae.moba.core.operators;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.logic.ButtonLogic;
import ch.awae.moba.core.model.logic.Logic;

public class CenterMacroOperator extends IOperator {

	private final Logic logic_1;
	private final Logic logic_2;

	public CenterMacroOperator() {
		super("center macros");

		Logic L1a = new ButtonLogic(ButtonMapping.C_S_1_A, true);
		Logic L1b = new ButtonLogic(ButtonMapping.C_S_1_B, true);
		Logic L2a = new ButtonLogic(ButtonMapping.C_S_2_A, true);
		Logic L2b = new ButtonLogic(ButtonMapping.C_S_2_B, true);
		Logic L4a = new ButtonLogic(ButtonMapping.C_S_4_A, true);
		Logic L4b = new ButtonLogic(ButtonMapping.C_S_4_B, true);

		logic_1 = L1a.or(L1b).and(L4a.or(L4b));
		logic_2 = L2a.or(L2b).and(L4a.or(L4b));

	}

	@Override
	public void update(@NonNull Model model) {
		if (logic_1.evaluate(model)) {
			model.paths.register(Path.C_S_2_B);
			model.paths.register(Path.C_S_3_A);
		}
		if (logic_2.evaluate(model)) {
			model.paths.register(Path.C_S_1_B);
			model.paths.register(Path.C_S_3_B);
		}
	}

}
