package ch.awae.moba.core.operators.impl;

import static ch.awae.moba.core.model.ButtonMapping.R_CLR_A;
import static ch.awae.moba.core.model.ButtonMapping.R_CLR_B;
import static ch.awae.moba.core.model.ButtonMapping.R_PTH_A;
import static ch.awae.moba.core.model.ButtonMapping.R_PTH_B;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_1;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_2;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_3;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_4;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.model.logic.Logic;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperator;

@Enabled(false)
public class RightSignalCloserOperator extends IOperator {

	private static final long ARMING_DELAY = 100;

	private final Logic _1, _2, _3, _4, _A, _B;
	private final Logic none;

	private boolean armed;
	private long armTime;

	public RightSignalCloserOperator() {
		super("right (clear signals)");

		none = Logic.count(0, R_TRK_1, R_TRK_2, R_TRK_3, R_TRK_4, R_PTH_A, R_PTH_B, R_CLR_A, R_CLR_B);
		Logic one = Logic.count(1, R_TRK_1, R_TRK_2, R_TRK_3, R_TRK_4, R_PTH_A, R_PTH_B, R_CLR_A, R_CLR_B);

		_1 = R_TRK_1.and(one);
		_2 = R_TRK_2.and(one);
		_3 = R_TRK_3.and(one);
		_4 = R_TRK_4.and(one);
		_A = R_PTH_A.and(one);
		_B = R_PTH_B.and(one);
	}

	@Override
	public void update(@NonNull Model model) {
		List<Path> paths = model.paths.getPaths(Sector.RIGHT);
		if (!armed) {
			if (none.evaluate(model)) {
				this.armed = true;
				this.armTime = System.currentTimeMillis();
			}
		} else if (none.evaluate(model) && ((System.currentTimeMillis() - this.armTime) < ARMING_DELAY)) {
			// do nothing
		} else if (_1.evaluate(model)) {
			if (paths.contains(Path.R_A_1_I))
				model.paths.register(Path.R_A_1_R);
			if (paths.contains(Path.R_B_1_I))
				model.paths.register(Path.R_B_1_R);
		} else if (_2.evaluate(model)) {
			if (paths.contains(Path.R_A_2_I))
				model.paths.register(Path.R_A_2_R);
			if (paths.contains(Path.R_B_2_I))
				model.paths.register(Path.R_B_2_R);
		} else if (_3.evaluate(model)) {
			if (paths.contains(Path.R_A_3_I))
				model.paths.register(Path.R_A_3_R);
			if (paths.contains(Path.R_B_3_I))
				model.paths.register(Path.R_B_3_R);
		} else if (_4.evaluate(model)) {
			if (paths.contains(Path.R_A_4_I))
				model.paths.register(Path.R_A_4_R);
			if (paths.contains(Path.R_B_4_I))
				model.paths.register(Path.R_B_4_R);
		} else if (_A.evaluate(model)) {
			if (paths.contains(Path.R_A_1_O))
				model.paths.register(Path.R_A_1_R);
			if (paths.contains(Path.R_A_2_O))
				model.paths.register(Path.R_A_2_R);
			if (paths.contains(Path.R_A_3_O))
				model.paths.register(Path.R_A_3_R);
			if (paths.contains(Path.R_A_4_O))
				model.paths.register(Path.R_A_4_R);
		} else if (_B.evaluate(model)) {
			if (paths.contains(Path.R_B_1_O))
				model.paths.register(Path.R_B_1_R);
			if (paths.contains(Path.R_B_2_O))
				model.paths.register(Path.R_B_2_R);
			if (paths.contains(Path.R_B_3_O))
				model.paths.register(Path.R_B_3_R);
			if (paths.contains(Path.R_B_4_O))
				model.paths.register(Path.R_B_4_R);
		} else {
			this.armed = false;
		}
	}

}
