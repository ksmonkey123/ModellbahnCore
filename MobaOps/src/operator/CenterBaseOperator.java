package operator;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

@Enabled(true)
@Loaded(true)
@Operator("center.base")
public class CenterBaseOperator implements IOperation {

    @External
    private Model model;

    private final ButtonMapping[] BUTTONS = { ButtonMapping.C_S_1_A, ButtonMapping.C_S_1_B,
            ButtonMapping.C_S_2_A, ButtonMapping.C_S_2_B, ButtonMapping.C_S_3_A,
            ButtonMapping.C_S_3_B, ButtonMapping.C_S_4_A, ButtonMapping.C_S_4_B };
    private final Path[]          PATHS   = { Path.C_S_1_A, Path.C_S_1_B, Path.C_S_2_A,
            Path.C_S_2_B, Path.C_S_3_A, Path.C_S_3_B, Path.C_S_4_A, Path.C_S_4_B };

    @Override
    public void update() {
        if (this.model.buttons.getState(ButtonMapping.C_CLEAR)) {
            this.model.paths.register(Path.C_CLEAR);
            return;
        }

        for (int i = 0; i < this.BUTTONS.length; i++) {
            if (this.model.buttons.getState(this.BUTTONS[i]))
                this.model.paths.register(this.PATHS[i]);
        }

    }

}
