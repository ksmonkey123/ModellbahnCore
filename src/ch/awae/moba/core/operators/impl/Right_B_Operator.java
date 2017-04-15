package ch.awae.moba.core.operators.impl;

import static ch.awae.moba.core.model.ButtonMapping.R_CLR_A;
import static ch.awae.moba.core.model.ButtonMapping.R_CLR_B;
import static ch.awae.moba.core.model.ButtonMapping.R_PTH_A;
import static ch.awae.moba.core.model.ButtonMapping.R_PTH_B;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_1;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_2;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_3;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_4;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.AOperator;

public class Right_B_Operator extends AOperator {

	private final static long DECORATOR_DELAY = 700;

	private final Logic _B, _1, _2, _3, _4;
	private final Logic _B_solo, _one_trk_solo;

	private final Logic B_1, B_2, B_3, B_4;

	private @Nullable Logic current;
	private boolean inbound;
	private long activeTime;
	private int trackID;
	private boolean processed;

	public Right_B_Operator() {
		super("right (b only)");

		Logic NC = R_CLR_A.or(R_CLR_B).not();
		Logic one_pth = Logic.count(1, R_PTH_A, R_PTH_B);
		Logic one_trk = Logic.count(1, R_TRK_1, R_TRK_2, R_TRK_3, R_TRK_4);

		this._B = R_PTH_B.and(one_pth).and(NC);
		this._1 = R_TRK_1.and(one_trk).and(NC);
		this._2 = R_TRK_2.and(one_trk).and(NC);
		this._3 = R_TRK_3.and(one_trk).and(NC);
		this._4 = R_TRK_4.and(one_trk).and(NC);

		this._B_solo = this._B.and(Logic.count(0, R_TRK_1, R_TRK_2, R_TRK_3, R_TRK_4));
		this._one_trk_solo = one_trk.and(Logic.count(0, R_PTH_A, R_PTH_B, R_CLR_A, R_CLR_B));

		this.B_1 = this._B.and(this._1);
		this.B_2 = this._B.and(this._2);
		this.B_3 = this._B.and(this._3);
		this.B_4 = this._B.and(this._4);
	}

	@Override
	public void update(@NonNull Model model) {
		Logic curr = this.current;
		if (curr == null) {
			// base mode
			if (this._B_solo.evaluate(model))
				// only C is pressed => inbound
				this.inbound = true;
			if (this._one_trk_solo.evaluate(model))
				// exactly one track is pressed => outbound
				this.inbound = false;
			// check for combo
			if (this.B_1.evaluate(model)) {
				model.paths.register(Path.R_B_1_R);
				this.trackID = 1;
				this.current = this.B_1;
				this.activeTime = System.currentTimeMillis();
			} else if (this.B_2.evaluate(model)) {
				model.paths.register(Path.R_B_2_R);
				this.trackID = 2;
				this.current = this.B_2;
				this.activeTime = System.currentTimeMillis();
			} else if (this.B_3.evaluate(model)) {
				model.paths.register(Path.R_B_3_R);
				this.trackID = 3;
				this.current = this.B_3;
				this.activeTime = System.currentTimeMillis();
			} else if (this.B_4.evaluate(model)) {
				model.paths.register(Path.R_B_4_R);
				this.trackID = 4;
				this.current = this.B_4;
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
						model.paths.register(this.inbound ? Path.R_B_1_I : Path.R_B_1_O);
						break;
					case 2:
						model.paths.register(this.inbound ? Path.R_B_2_I : Path.R_B_2_O);
						break;
					case 3:
						model.paths.register(this.inbound ? Path.R_B_3_I : Path.R_B_3_O);
						break;
					case 4:
						model.paths.register(this.inbound ? Path.R_B_4_I : Path.R_B_4_O);
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