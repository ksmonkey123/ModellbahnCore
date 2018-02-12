package ch.awae.moba.operator;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.moba.core.util.Props;
import ch.awae.moba.core.util.Utils;
import ch.awae.utils.logic.Logic;

@Enabled
@Operator("system.reset")
public class ResetOperator implements IOperation {

    private final static Props props    = Configs.load("resetter");
    private final static long  ARM_TIME = props.getInt("arm_time");

    private final Logic trigger = Logic.count(3, //
            ButtonProvider.getButton(Sector.BOTTOM, "clear"),
            ButtonProvider.getButton(Sector.CENTER, "clear"),
            ButtonProvider.getButton(Sector.LEFT, "clear"),
            ButtonProvider.getButton(Sector.RIGHT, "clear_A"),
            ButtonProvider.getButton(Sector.RIGHT, "clear_B"));

    private boolean armed;
    private long    armTime;

    @Override
    public void update() {
        boolean signal = this.trigger.evaluate();

        if (signal) {
            if (this.armed) {
                long deltaT = System.currentTimeMillis() - this.armTime;
                if (deltaT > ARM_TIME) {
                    Utils.doReboot();
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
