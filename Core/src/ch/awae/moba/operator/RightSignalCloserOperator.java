package ch.awae.moba.operator;

import static ch.awae.moba.core.model.ButtonMapping.R_CLR_A;
import static ch.awae.moba.core.model.ButtonMapping.R_CLR_B;
import static ch.awae.moba.core.model.ButtonMapping.R_PTH_A;
import static ch.awae.moba.core.model.ButtonMapping.R_PTH_B;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_1;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_2;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_3;
import static ch.awae.moba.core.model.ButtonMapping.R_TRK_4;

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
@Operator("right.sigclose")
public class RightSignalCloserOperator implements IOperation {

    @External
    private Model model;

    private static final long ARMING_DELAY = 100;

    private final Logic _1, _2, _3, _4, _A, _B;
    private final Logic none;

    private boolean armed;
    private long    armTime;

    public RightSignalCloserOperator() {
        this.none = Logic.count(0, R_TRK_1, R_TRK_2, R_TRK_3, R_TRK_4, R_PTH_A, R_PTH_B, R_CLR_A,
                R_CLR_B);
        Logic one = Logic.count(1, R_TRK_1, R_TRK_2, R_TRK_3, R_TRK_4, R_PTH_A, R_PTH_B, R_CLR_A,
                R_CLR_B);

        this._1 = R_TRK_1.and(one);
        this._2 = R_TRK_2.and(one);
        this._3 = R_TRK_3.and(one);
        this._4 = R_TRK_4.and(one);
        this._A = R_PTH_A.and(one);
        this._B = R_PTH_B.and(one);
    }

    @Override
    public void update() {
        List<Path> paths = this.model.paths.getPaths(Sector.RIGHT);
        if (!this.armed) {
            if (this.none.evaluate(this.model)) {
                this.armed = true;
                this.armTime = System.currentTimeMillis();
            }
        } else if (this.none.evaluate(this.model)
                && ((System.currentTimeMillis() - this.armTime) < ARMING_DELAY)) {
            // do nothing
        } else if (this._1.evaluate(this.model)) {
            if (paths.contains(Path.R_A_1_I))
                this.model.paths.register(Path.R_A_1_R);
            if (paths.contains(Path.R_B_1_I))
                this.model.paths.register(Path.R_B_1_R);
        } else if (this._2.evaluate(this.model)) {
            if (paths.contains(Path.R_A_2_I))
                this.model.paths.register(Path.R_A_2_R);
            if (paths.contains(Path.R_B_2_I))
                this.model.paths.register(Path.R_B_2_R);
        } else if (this._3.evaluate(this.model)) {
            if (paths.contains(Path.R_A_3_I))
                this.model.paths.register(Path.R_A_3_R);
            if (paths.contains(Path.R_B_3_I))
                this.model.paths.register(Path.R_B_3_R);
        } else if (this._4.evaluate(this.model)) {
            if (paths.contains(Path.R_A_4_I))
                this.model.paths.register(Path.R_A_4_R);
            if (paths.contains(Path.R_B_4_I))
                this.model.paths.register(Path.R_B_4_R);
        } else if (this._A.evaluate(this.model)) {
            if (paths.contains(Path.R_A_1_O))
                this.model.paths.register(Path.R_A_1_R);
            if (paths.contains(Path.R_A_2_O))
                this.model.paths.register(Path.R_A_2_R);
            if (paths.contains(Path.R_A_3_O))
                this.model.paths.register(Path.R_A_3_R);
            if (paths.contains(Path.R_A_4_O))
                this.model.paths.register(Path.R_A_4_R);
        } else if (this._B.evaluate(this.model)) {
            if (paths.contains(Path.R_B_1_O))
                this.model.paths.register(Path.R_B_1_R);
            if (paths.contains(Path.R_B_2_O))
                this.model.paths.register(Path.R_B_2_R);
            if (paths.contains(Path.R_B_3_O))
                this.model.paths.register(Path.R_B_3_R);
            if (paths.contains(Path.R_B_4_O))
                this.model.paths.register(Path.R_B_4_R);
        } else {
            this.armed = false;
        }
    }

}
