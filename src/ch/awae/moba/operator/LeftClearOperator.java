package ch.awae.moba.operator;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.External;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;

@Enabled
@Loaded
@Operator("left.clear")
public class LeftClearOperator implements IOperation {

    @External
    private Model model;

    private Logic clear = ButtonProvider.getButton(Sector.LEFT, "clear");

    @Override
    public void update() {
        if (this.clear.evaluate(this.model))
            this.model.paths.register(Path.L_CLEAR);
    }

}
