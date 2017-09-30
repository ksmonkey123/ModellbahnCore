package ch.awae.moba.operator;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;

@Enabled
@Loaded
@Operator("right.clear")
public class RightClearOperator implements IOperation {

    private final Model model = Model.getInstance();

    private Logic clear_a, clear_b;

    {
        ButtonProvider p = new ButtonProvider(Sector.RIGHT);

        this.clear_a = p.button("clear_A");
        this.clear_b = p.button("clear_B");
    }

    @Override
    public void update() {
        if (this.clear_a.evaluate())
            this.model.paths.register(Path.R_CLR_A);
        if (this.clear_b.evaluate())
            this.model.paths.register(Path.R_CLR_B);
    }

}
