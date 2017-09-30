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
@Operator("center.macro")
public class CenterMacroOperator implements IOperation {

    private final Model model = Model.getInstance();

    private final Logic logic_1;
    private final Logic logic_2;

    public CenterMacroOperator() {
        ButtonProvider provider = new ButtonProvider(Sector.CENTER);

        Logic S0 = provider.group("switch_0").any();
        Logic S1 = provider.group("switch_1").any();
        Logic S3 = provider.group("switch_3").any();

        this.logic_1 = S0.and(S3);
        this.logic_2 = S1.and(S3);
    }

    @Override
    public void update() {
        if (this.logic_1.evaluate()) {
            this.model.paths.register(Path.C_S_2_B);
            this.model.paths.register(Path.C_S_3_A);
        }
        if (this.logic_2.evaluate()) {
            this.model.paths.register(Path.C_S_1_B);
            this.model.paths.register(Path.C_S_3_B);
        }
    }

}
