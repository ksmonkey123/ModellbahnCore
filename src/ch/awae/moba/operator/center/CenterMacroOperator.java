package ch.awae.moba.operator.center;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;

@Enabled
@Operator("center.macro")
public class CenterMacroOperator implements IOperation {

    private final Logic logic_1, logic_2;
    private final Path  path_1b, path_2b, path_3a, path_3b;

    public CenterMacroOperator() {
        ButtonProvider provider = new ButtonProvider(Sector.CENTER);
        PathProvider pathProvider = PathProvider.getInstance();

        Logic S0 = provider.group("switch_0").any();
        Logic S1 = provider.group("switch_1").any();
        Logic S3 = provider.group("switch_3").any();

        this.logic_1 = S0.and(S3);
        this.logic_2 = S1.and(S3);

        this.path_1b = pathProvider.getPath("center.1b");
        this.path_2b = pathProvider.getPath("center.2b");
        this.path_3a = pathProvider.getPath("center.3a");
        this.path_3b = pathProvider.getPath("center.3b");
    }

    @Override
    public void update() {
        if (logic_1.evaluate()) {
            path_2b.issue(true);
            path_3a.issue(true);
        }
        if (logic_2.evaluate()) {
            path_1b.issue(true);
            path_3b.issue(true);
        }
    }

}
