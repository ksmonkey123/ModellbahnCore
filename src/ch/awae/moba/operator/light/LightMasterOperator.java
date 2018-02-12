package ch.awae.moba.operator.light;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;
import ch.awae.utils.logic.LogicGroup;

@Enabled
@Operator("light.master")
public class LightMasterOperator implements IOperation {

    private final ButtonProvider p = new ButtonProvider(Sector.LIGHT);

    private final LogicGroup domain = p.group("__buttons");

    private final Logic off = p.group("button_01").strict(domain).edge();
    private final Logic on  = p.group("button_02").strict(domain).edge();

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
