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
@Operator("left.AB")
public class Left_AB_Operator implements IOperation {

    @External
    private Model model;

    private final Logic A_1;
    private final Logic B_1;
    private final Logic B_2;

    public Left_AB_Operator() {
        Logic one_pth = Logic.count(1, L_PTH_A, L_PTH_B, L_PTH_C);
        Logic one_trk = Logic.count(1, L_TRK_1, L_TRK_2, L_TRK_3, L_TRK_4);

        Logic NC = L_CLEAR.not();

        Logic _A = L_PTH_A.and(one_pth).and(NC);
        Logic _B = L_PTH_B.and(one_pth).and(NC);
        Logic _1 = L_TRK_1.and(one_trk).and(NC);
        Logic _2 = L_TRK_2.and(one_trk).and(NC);

        this.A_1 = _A.and(_1);
        this.B_1 = _B.and(_1);
        this.B_2 = _B.and(_2);
    }

    @SuppressWarnings("null")
    @Override
    public void update() {
        if (this.A_1.evaluate(this.model))
            this.model.paths.register(Path.L_A_1_R);
        if (this.B_1.evaluate(this.model))
            this.model.paths.register(Path.L_B_1_R);
        if (this.B_2.evaluate(this.model))
            this.model.paths.register(Path.L_B_2_R);
    }

}
