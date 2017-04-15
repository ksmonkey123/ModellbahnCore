package ch.awae.moba.core.test;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

@Loaded
@Enabled
@Operator("test")
public class TestOperator implements IOperation {

    @External
    private Model model;

    @Override
    public void update() {
        // noop
    }

}
