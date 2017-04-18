package operator;

import static ch.awae.moba.core.model.ButtonMapping.L_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_A;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_B;
import static ch.awae.moba.core.model.ButtonMapping.L_PTH_C;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_1;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_2;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_3;
import static ch.awae.moba.core.model.ButtonMapping.L_TRK_4;

import java.util.List;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

@Enabled(false)
@Loaded
@Operator("left.sigclose")
public class LeftSignalCloserOperator implements IOperation {

    @External
    private Model model;

    private static final long ARMING_DELAY = 100;

    private final Logic _1, _2, _3, _4, _C;
    private final Logic none;

    private boolean armed;
    private long    armTime;

    public LeftSignalCloserOperator() {
        this.none = Logic.count(0, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4, L_PTH_A, L_PTH_B, L_PTH_C,
                L_CLEAR);
        Logic one = Logic.count(1, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4, L_PTH_A, L_PTH_B, L_PTH_C,
                L_CLEAR);

        this._1 = L_TRK_1.and(one);
        this._2 = L_TRK_2.and(one);
        this._3 = L_TRK_3.and(one);
        this._4 = L_TRK_4.and(one);
        this._C = L_PTH_C.and(one);
    }

    @SuppressWarnings("null")
    @Override
    public void update() {
        List<Path> paths = this.model.paths.getPaths(Sector.LEFT);
        if (!this.armed) {
            if (this.none.evaluate(this.model)) {
                this.armed = true;
                this.armTime = System.currentTimeMillis();
            }
        } else if (this.none.evaluate(this.model)
                && ((System.currentTimeMillis() - this.armTime) < ARMING_DELAY)) {
            // do nothing
        } else if (this._1.evaluate(this.model)) {
            if (paths.contains(Path.L_C_1_I))
                this.model.paths.register(Path.L_C_1_R);
        } else if (this._2.evaluate(this.model)) {
            if (paths.contains(Path.L_C_2_I))
                this.model.paths.register(Path.L_C_2_R);
        } else if (this._3.evaluate(this.model)) {
            if (paths.contains(Path.L_C_3_I))
                this.model.paths.register(Path.L_C_3_R);
        } else if (this._4.evaluate(this.model)) {
            if (paths.contains(Path.L_C_4_I))
                this.model.paths.register(Path.L_C_4_R);
        } else if (this._C.evaluate(this.model)) {
            if (paths.contains(Path.L_C_1_O))
                this.model.paths.register(Path.L_C_1_R);
            if (paths.contains(Path.L_C_2_O))
                this.model.paths.register(Path.L_C_2_R);
            if (paths.contains(Path.L_C_3_O))
                this.model.paths.register(Path.L_C_3_R);
            if (paths.contains(Path.L_C_4_O))
                this.model.paths.register(Path.L_C_4_R);
        } else {
            this.armed = false;
        }
    }

}
