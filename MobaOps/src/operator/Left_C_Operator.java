package operator;

import static ch.awae.moba.core.model.ButtonMapping.L_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_A;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_B;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_C;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_1;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_2;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_3;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_4;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

@Enabled
@Loaded
@Operator("left.C")
public class Left_C_Operator implements IOperation {

    @External
    private Model model;

    private final static long DECORATOR_DELAY = 700;

    private final Logic _C, _1, _2, _3, _4;
    private final Logic _C_solo, _one_trk_solo;

    private final Logic C_1, C_2, C_3, C_4;

    private Logic   current;
    private boolean inbound;
    private long    activeTime;
    private int     trackID;
    private boolean processed;

    public Left_C_Operator() {
        Logic NC = L_CLEAR.not();
        Logic one_pth = Logic.count(1, L_PTH_A, L_PTH_B, L_PTH_C);
        Logic one_trk = Logic.count(1, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4);

        this._C = L_PTH_C.and(one_pth).and(NC);
        this._1 = L_TRK_1.and(one_trk).and(NC);
        this._2 = L_TRK_2.and(one_trk).and(NC);
        this._3 = L_TRK_3.and(one_trk).and(NC);
        this._4 = L_TRK_4.and(one_trk).and(NC);

        this._C_solo = this._C.and(Logic.count(0, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4));
        this._one_trk_solo = one_trk.and(Logic.count(0, L_PTH_A, L_PTH_B, L_PTH_C, L_CLEAR));

        this.C_1 = this._C.and(this._1);
        this.C_2 = this._C.and(this._2);
        this.C_3 = this._C.and(this._3);
        this.C_4 = this._C.and(this._4);
    }

    @Override
    public void update() {
        Logic curr = this.current;
        if (curr == null) {
            // base mode
            if (this._C_solo.evaluate(this.model))
                // only C is pressed => inbound
                this.inbound = true;
            if (this._one_trk_solo.evaluate(this.model))
                // exactly one track is pressed => outbound
                this.inbound = false;
            // check for combo
            if (this.C_1.evaluate(this.model)) {
                this.model.paths.register(Path.L_C_1_R);
                this.trackID = 1;
                this.current = this.C_1;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_2.evaluate(this.model)) {
                this.model.paths.register(Path.L_C_2_R);
                this.trackID = 2;
                this.current = this.C_2;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_3.evaluate(this.model)) {
                this.model.paths.register(Path.L_C_3_R);
                this.trackID = 3;
                this.current = this.C_3;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_4.evaluate(this.model)) {
                this.model.paths.register(Path.L_C_4_R);
                this.trackID = 4;
                this.current = this.C_4;
                this.activeTime = System.currentTimeMillis();
            }
        } else {
            // active mode => check if current still holds
            if (curr.evaluate(this.model)) {
                // still good => if time reached, decorate
                long deltaT = System.currentTimeMillis() - this.activeTime;
                if ((deltaT > DECORATOR_DELAY) && !this.processed) {
                    this.processed = true;
                    // decorate
                    switch (this.trackID) {
                        case 1:
                            this.model.paths.register(this.inbound ? Path.L_C_1_I : Path.L_C_1_O);
                            break;
                        case 2:
                            this.model.paths.register(this.inbound ? Path.L_C_2_I : Path.L_C_2_O);
                            break;
                        case 3:
                            this.model.paths.register(this.inbound ? Path.L_C_3_I : Path.L_C_3_O);
                            break;
                        case 4:
                            this.model.paths.register(this.inbound ? Path.L_C_4_I : Path.L_C_4_O);
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
