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
@Operator("left.clear")
public class LeftClearOperator implements IOperation {

    @External
    private Model model;

    @SuppressWarnings("null")
    @Override
    public void update() {
        if (ButtonMapping.L_CLEAR.evaluate(this.model))
            this.model.paths.register(Path.L_CLEAR);
    }

}
