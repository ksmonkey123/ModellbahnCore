package ch.awae.moba.core.operators.impl;

import static ch.awae.moba.core.model.ButtonMapping.L_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_A;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_B;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_C;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_1;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_2;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_3;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_4;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.model.logic.Logic;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperator;

@Enabled(false)
public class LeftSignalCloserOperator extends IOperator {

	private static final long ARMING_DELAY = 100;

	private final Logic _1, _2, _3, _4, _C;
	private final Logic none;

	private boolean armed;
	private long armTime;

	public LeftSignalCloserOperator() {
		super("left (clear signals)");

		none = Logic.count(0, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4, L_PTH_A, L_PTH_B, L_PTH_C, L_CLEAR);
		Logic one = Logic.count(1, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4, L_PTH_A, L_PTH_B, L_PTH_C, L_CLEAR);

		_1 = L_TRK_1.and(one);
		_2 = L_TRK_2.and(one);
		_3 = L_TRK_3.and(one);
		_4 = L_TRK_4.and(one);
		_C = L_PTH_C.and(one);
	}

	@Override
	public void update(@NonNull Model model) {
		List<Path> paths = model.paths.getPaths(Sector.LEFT);
		if (!armed) {
			if (none.evaluate(model)) {
				this.armed = true;
				this.armTime = System.currentTimeMillis();
			}
		} else if (none.evaluate(model) && ((System.currentTimeMillis() - this.armTime) < ARMING_DELAY)) {
			// do nothing
		} else if (_1.evaluate(model)) {
			if (paths.contains(Path.L_C_1_I))
				model.paths.register(Path.L_C_1_R);
		} else if (_2.evaluate(model)) {
			if (paths.contains(Path.L_C_2_I))
				model.paths.register(Path.L_C_2_R);
		} else if (_3.evaluate(model)) {
			if (paths.contains(Path.L_C_3_I))
				model.paths.register(Path.L_C_3_R);
		} else if (_4.evaluate(model)) {
			if (paths.contains(Path.L_C_4_I))
				model.paths.register(Path.L_C_4_R);
		} else if (_C.evaluate(model)) {
			if (paths.contains(Path.L_C_1_O))
				model.paths.register(Path.L_C_1_R);
			if (paths.contains(Path.L_C_2_O))
				model.paths.register(Path.L_C_2_R);
			if (paths.contains(Path.L_C_3_O))
				model.paths.register(Path.L_C_3_R);
			if (paths.contains(Path.L_C_4_O))
				model.paths.register(Path.L_C_4_R);
		} else {
			this.armed = false;
		}
	}

}
