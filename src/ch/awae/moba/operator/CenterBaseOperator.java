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

@Enabled(true)
@Loaded(true)
@Operator("center.base")
public class CenterBaseOperator implements IOperation {

    @External
    private Model model;

    private final ButtonProvider provider = new ButtonProvider(Sector.CENTER);

    private final Logic[] buttons = this.provider.group("buttons").toArray();
    private final Logic   clear   = this.provider.button("clear");

    private final Path[] PATHS = { Path.C_S_1_A, Path.C_S_1_B, Path.C_S_2_A, Path.C_S_2_B,
            Path.C_S_3_A, Path.C_S_3_B, Path.C_S_4_A, Path.C_S_4_B };

    @Override
    public void update() {
        if (this.clear.evaluate(this.model)) {
            this.model.paths.register(Path.C_CLEAR);
            return;
        }

        for (int i = 0; i < this.buttons.length; i++) {
            if (this.buttons[i].evaluate(this.model))
                this.model.paths.register(this.PATHS[i]);
        }

    }

}
