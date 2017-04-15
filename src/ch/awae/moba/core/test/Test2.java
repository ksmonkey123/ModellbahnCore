package ch.awae.moba.core.test;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

@Loaded
@Enabled
@Operator("test2")
public class Test2 implements IOperation {

    @External
    private Model model;

    @Operator("test")
    private IOperator operator;

    @Override
    public void update() {
        // no op
    }

}
