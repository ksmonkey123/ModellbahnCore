package operator;

import java.util.List;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

@Enabled(false)
@Loaded(true)
@Operator("bottom.qm")
public class BottomQuickMode implements IOperation {

    @External
    private Model model;

    private static final long BLINK_TIME = 500;

    @Override
    public void update() {
        List<Path> paths = this.model.paths.getPaths(Sector.BOTTOM);

        if (ButtonMapping.B_CLEAR.evaluate(this.model)) {
            for (Path path : this.model.paths.getPaths(Sector.BOTTOM)) {
                if (path != Path.B_DUMMY_L && !path.forced)
                    this.model.paths.unregister(path);
            }
        }

        if (paths.isEmpty() || (paths.size() == 1) && paths.contains(Path.B_DUMMY_L)) {
            if (System.currentTimeMillis() % (2 * BLINK_TIME) > BLINK_TIME)
                this.model.paths.register(Path.B_DUMMY_L);
            else
                this.model.paths.unregister(Path.B_DUMMY_L);
        }
        for (int i = 0; i < 10; i++) {
            Path path = Path.BOTTOM_LEFT[i];
            assert path != null;
            ButtonMapping button = ButtonMapping.BOTTOM_TRACKS[i];
            if (button.evaluate(this.model)) {
                this.model.paths.register(path);
                return;
            }
        }
    }

}
