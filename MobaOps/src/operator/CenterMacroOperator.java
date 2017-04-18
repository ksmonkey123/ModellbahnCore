package operator;

import ch.awae.moba.core.logic.Logic;
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
@Operator("center.macro")
public class CenterMacroOperator implements IOperation {

    @External
    private Model model;

    private final Logic logic_1;
    private final Logic logic_2;

    public CenterMacroOperator() {
        Logic L1a = ButtonMapping.C_S_1_A;
        Logic L1b = ButtonMapping.C_S_1_B;
        Logic L2a = ButtonMapping.C_S_2_A;
        Logic L2b = ButtonMapping.C_S_2_B;
        Logic L4a = ButtonMapping.C_S_4_A;
        Logic L4b = ButtonMapping.C_S_4_B;

        this.logic_1 = L1a.or(L1b).and(L4a.or(L4b));
        this.logic_2 = L2a.or(L2b).and(L4a.or(L4b));
    }

    @SuppressWarnings("null")
    @Override
    public void update() {
        if (this.logic_1.evaluate(this.model)) {
            this.model.paths.register(Path.C_S_2_B);
            this.model.paths.register(Path.C_S_3_A);
        }
        if (this.logic_2.evaluate(this.model)) {
            this.model.paths.register(Path.C_S_1_B);
            this.model.paths.register(Path.C_S_3_B);
        }
    }

}
