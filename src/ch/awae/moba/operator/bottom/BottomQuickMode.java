package ch.awae.moba.operator.bottom;

import java.util.List;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.moba.core.util.Props;
import ch.awae.utils.logic.Logic;

@Operator("bottom.qm")
public class BottomQuickMode implements IOperation {

    private static final Props props = Configs.load("bottom.qma");

    private static final long BLINK_TIME = props.getInt("qm.blink_time");

    private final ButtonProvider provider     = new ButtonProvider(Sector.BOTTOM);
    private final PathProvider   pathProvider = PathProvider.getInstance();

    private final Logic[] tracks = this.provider.group("all_tracks").toArray();
    private final Logic   clear  = this.provider.button("clear");

    private final Path   dummy       = pathProvider.getPath("bottom.dummy_l");
    private final Path[] BOTTOM_LEFT = pathProvider.getPaths("bottom.01", "bottom.02", "bottom.03",
            "bottom.04", "bottom.05", "bottom.06", "bottom.07", "bottom.08", "bottom.09",
            "bottom.10");

    private boolean previousBlinkState = false;

    @Override
    public void update() {
        List<Path> paths = Model.getActivePaths(Sector.BOTTOM);

        if (this.clear.evaluate()) {
            for (Path path : paths) {
                if (path != dummy && path.priority == 0)
                    path.issue(false);
            }
        }

        if (paths.isEmpty() || ((paths.size() == 1) && paths.contains(dummy))) {
            boolean next = System.currentTimeMillis() % (2 * BLINK_TIME) > BLINK_TIME;
            if (next != previousBlinkState)
                dummy.issue(next);
            previousBlinkState = next;
        }

        for (int i = 0; i < 10; i++) {
            Path path = BOTTOM_LEFT[i];
            assert path != null;
            Logic button = this.tracks[i];
            if (button.evaluate()) {
                path.issue(true);
                return;
            }
        }
    }

}
