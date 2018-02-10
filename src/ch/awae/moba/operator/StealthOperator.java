package ch.awae.moba.operator;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;

@Enabled
@Loaded
@Operator("light.stealth")
public class StealthOperator implements IOperation {

    private Logic   stealth = ButtonProvider.getButton(Sector.LIGHT, "stealth_switch");
    private boolean active  = false;

    @Override
    public void update() {
        if (stealth.evaluate()) {
            if (!active)
                Model.toggleStealthMode();
            active = true;
        } else {
            active = false;
        }
    }

}
