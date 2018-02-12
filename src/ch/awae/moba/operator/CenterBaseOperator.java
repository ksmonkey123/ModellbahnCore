package ch.awae.moba.operator;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;

@Enabled
@Operator("center.base")
public class CenterBaseOperator implements IOperation {

    private final ButtonProvider provider     = new ButtonProvider(Sector.CENTER);
    private final PathProvider   pathProvider = PathProvider.getInstance();

    private final Logic[] buttons = this.provider.group("buttons").toArray();
    private final Logic   clear   = this.provider.button("clear");

    private final Path   p_clear = pathProvider.getPath("center.clear");
    private final Path[] paths   = pathProvider.getPaths("center.1a", "center.1b", "center.2a",
            "center.2b", "center.3a", "center.3b", "center.4a", "center.4b");

    @Override
    public void update() {
        if (clear.evaluate()) {
            p_clear.issue(true);
            return;
        }

        for (int i = 0; i < buttons.length; i++)
            if (buttons[i].evaluate())
                paths[i].issue(true);
    }

}
