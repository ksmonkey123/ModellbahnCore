package ch.awae.moba.operator.light;

import ch.awae.moba.core.model.LightButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;

@Enabled
@Operator("light.master")
public class LightMasterOperator implements IOperation {

    private final Logic off = LightButtonProvider.button("button_01");
    private final Logic on  = LightButtonProvider.button("button_02");

    @Override
    public void update() {
        if (on.evaluate())
            for (int decoder = 0; decoder < 16; decoder++)
                Model.lights().enable(decoder, (byte) 0xff);
        if (off.evaluate())
            for (int decoder = 0; decoder < 16; decoder++)
                Model.lights().disable(decoder, (byte) 0xff);
    }

}
