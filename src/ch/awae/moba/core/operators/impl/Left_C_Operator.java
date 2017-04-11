package ch.awae.moba.core.operators.impl;

import static ch.awae.moba.core.model.ButtonMapping.L_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_A;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_B;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_C;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_1;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_2;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_3;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_4;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.logic.Logic;
import ch.awae.moba.core.operators.IOperator;

public class Left_C_Operator extends IOperator {

	private final static long DECORATOR_DELAY = 700;

	private final Logic _C, _1, _2, _3, _4;
	private final Logic _C_solo, _one_trk_solo;

	private final Logic C_1, C_2, C_3, C_4;

	private @Nullable Logic current;
	private boolean inbound;
	private long activeTime;
	private int trackID;
	private boolean processed;

	public Left_C_Operator() {
		super("left (c only)");

		Logic NC = L_CLEAR.not();
		Logic one_pth = Logic.count(1, L_PTH_A, L_PTH_B, L_PTH_C);
		Logic one_trk = Logic.count(1, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4);

		_C = L_PTH_C.and(one_pth).and(NC);
		_1 = L_TRK_1.and(one_trk).and(NC);
		_2 = L_TRK_2.and(one_trk).and(NC);
		_3 = L_TRK_3.and(one_trk).and(NC);
		_4 = L_TRK_4.and(one_trk).and(NC);

		_C_solo = _C.and(Logic.count(0, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4));
		_one_trk_solo = one_trk.and(Logic.count(0, L_PTH_A, L_PTH_B, L_PTH_C, L_CLEAR));

		C_1 = _C.and(_1);
		C_2 = _C.and(_2);
		C_3 = _C.and(_3);
		C_4 = _C.and(_4);
	}

	@Override
	public void update(@NonNull Model model) {
		Logic curr = this.current;
		if (curr == null) {
			// base mode
			if (_C_solo.evaluate(model))
				// only C is pressed => inbound
				this.inbound = true;
			if (_one_trk_solo.evaluate(model))
				// exactly one track is pressed => outbound
				this.inbound = false;
			// check for combo
			if (C_1.evaluate(model)) {
				model.paths.register(Path.L_C_1_R);
				this.trackID = 1;
				this.current = C_1;
				this.activeTime = System.currentTimeMillis();
			} else if (C_2.evaluate(model)) {
				model.paths.register(Path.L_C_2_R);
				this.trackID = 2;
				this.current = C_2;
				this.activeTime = System.currentTimeMillis();
			} else if (C_3.evaluate(model)) {
				model.paths.register(Path.L_C_3_R);
				this.trackID = 3;
				this.current = C_3;
				this.activeTime = System.currentTimeMillis();
			} else if (C_4.evaluate(model)) {
				model.paths.register(Path.L_C_4_R);
				this.trackID = 4;
				this.current = C_4;
				this.activeTime = System.currentTimeMillis();
			}
		} else {
			// active mode => check if current still holds
			if (curr.evaluate(model)) {
				// still good => if time reached, decorate
				long deltaT = System.currentTimeMillis() - this.activeTime;
				if ((deltaT > DECORATOR_DELAY) && !this.processed) {
					this.processed = true;
					// decorate
					switch (this.trackID) {
					case 1:
						model.paths.register(this.inbound ? Path.L_C_1_I : Path.L_C_1_O);
						break;
					case 2:
						model.paths.register(this.inbound ? Path.L_C_2_I : Path.L_C_2_O);
						break;
					case 3:
						model.paths.register(this.inbound ? Path.L_C_3_I : Path.L_C_3_O);
						break;
					case 4:
						model.paths.register(this.inbound ? Path.L_C_4_I : Path.L_C_4_O);
						break;
					default:
						// do nothing
					}
				}

			} else {
				// degraded => return to base mode
				this.trackID = 0;
				this.current = null;
				this.inbound = false;
				this.processed = false;
			}
		}
	}

}
