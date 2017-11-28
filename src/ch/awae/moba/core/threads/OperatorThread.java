package ch.awae.moba.core.threads;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.util.Registries;

public class OperatorThread extends AThreaded {

    public OperatorThread() {
        super("operator");
    }

    @Override
    protected void step() throws InterruptedException {
        for (String name : Registries.operators.getNames()) {
            IOperator operator = Registries.operators.get(name);
            if (operator != null && operator.isActive())
                operator.update();
        }
        Model.executeCommands();
    }

}
