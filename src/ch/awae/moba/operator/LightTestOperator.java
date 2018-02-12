package ch.awae.moba.operator;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;
import ch.awae.utils.logic.LogicGroup;

@Enabled
@Operator("light.test")
public class LightTestOperator implements IOperation {

    private final ButtonProvider p = new ButtonProvider(Sector.LIGHT);

    private LogicGroup domain = p.group("__buttons");
    private Logic      button = p.group("button_15").strict(domain).edge();

    private boolean state = false;

    @Override
    public void update() {
        if (button.evaluate()) {
            state = !state;
            Model.lights().setState(15, 0, state);
        }
    }

}