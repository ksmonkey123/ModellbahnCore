package ch.awae.moba.operator.light;

import ch.awae.moba.core.model.LightButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;

@Enabled
@Operator("light.test")
public class LightTestOperator implements IOperation {

    private Logic button = LightButtonProvider.button("button_15");

    @Override
    public void update() {
        if (button.evaluate()) {
            Model.lights().togglePin(15, 0);
        }
    }

}
