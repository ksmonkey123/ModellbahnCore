package ch.awae.moba.operator;

import static ch.awae.moba.core.model.ButtonMapping.B_TR_01;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_02;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_03;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_04;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_05;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_06;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_07;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_08;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_09;
import static ch.awae.moba.core.model.ButtonMapping.B_TR_10;

import java.util.List;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.External;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;

@Enabled(false)
@Loaded(true)
@Operator("bottom.qm")
public class BottomQuickMode implements IOperation {

    @External
    private Model model;

    private static final long BLINK_TIME = 500;

    public static final ButtonMapping[] BOTTOM_TRACKS = { B_TR_01, B_TR_02, B_TR_03, B_TR_04,
            B_TR_05, B_TR_06, B_TR_07, B_TR_08, B_TR_09, B_TR_10 };

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
            ButtonMapping button = BOTTOM_TRACKS[i];
            if (button.evaluate(this.model)) {
                this.model.paths.register(path);
                return;
            }
        }
    }

}
