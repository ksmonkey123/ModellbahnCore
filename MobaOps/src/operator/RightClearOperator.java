package operator;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

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
