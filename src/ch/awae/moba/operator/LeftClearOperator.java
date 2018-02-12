package ch.awae.moba.operator;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;

@Enabled
@Loaded
@Operator("left.clear")
public class LeftClearOperator implements IOperation {

    private Logic clear   = ButtonProvider.getButton(Sector.LEFT, "clear");
    private Path  p_clear = PathProvider.getInstance().getPath("left.clear");

    @Override
    public void update() {
        if (clear.evaluate())
            p_clear.issue(true);
    }

}
