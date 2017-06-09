package ch.awae.moba.operator;

import static ch.awae.moba.core.model.ButtonMapping.B_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.C_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.L_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.R_CLR_A;
import static ch.awae.moba.core.model.ButtonMapping.R_CLR_B;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.External;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;
import ch.awae.moba.core.util.Utils;

@Enabled
@Loaded
@Operator("system.reset")
public class ResetOperator implements IOperation {

    @External
    private Model model;

    private final static long ARM_TIME = 5000;

    private final Logic trigger = Logic.count(3, B_CLEAR, L_CLEAR, C_CLEAR, R_CLR_A, R_CLR_B);
    private boolean     armed;
    private long        armTime;

    @Override
    public void update() {
        boolean signal = this.trigger.evaluate(this.model);

        if (signal) {
            if (this.armed) {
                long deltaT = System.currentTimeMillis() - this.armTime;
                if (deltaT > ARM_TIME) {
                    Utils.doReboot(this.model);
                }
            } else {
                this.armed = true;
                this.armTime = System.currentTimeMillis();
            }
        } else {
            this.armed = false;
        }

    }

}
