package ch.awae.moba.operator.right;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;

@Enabled
@Operator("right.clear")
public class RightClearOperator implements IOperation {

    private final Logic clear_a, clear_b;
    private final Path  p_clear_a, p_clear_b;

    {
        ButtonProvider p = new ButtonProvider(Sector.RIGHT);
        PathProvider pp = PathProvider.getInstance();

        this.clear_a = p.button("clear_A");
        this.clear_b = p.button("clear_B");

        this.p_clear_a = pp.getPath("right.clear_A");
        this.p_clear_b = pp.getPath("right.clear_B");
    }

    @Override
    public void update() {
        if (clear_a.evaluate())
            p_clear_a.issue(true);
        if (clear_b.evaluate())
            p_clear_b.issue(true);
    }

}
