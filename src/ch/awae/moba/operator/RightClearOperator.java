package ch.awae.moba.operator;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.External;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;

@Enabled
@Loaded
@Operator("right.clear")
public class RightClearOperator implements IOperation {

    @External
    private Model model;

    @Override
    public void update() {
        if (ButtonMapping.R_CLR_A.evaluate(this.model))
            this.model.paths.register(Path.R_CLR_A);
        if (ButtonMapping.R_CLR_B.evaluate(this.model))
            this.model.paths.register(Path.R_CLR_B);
    }

}
