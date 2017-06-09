package ch.awae.moba.operator;

import java.util.List;

import ch.awae.moba.core.Configs;
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
import ch.awae.moba.core.util.Props;

@Enabled(false)
@Loaded(true)
@Operator("bottom.qm")
public class BottomQuickMode implements IOperation {

    @External
    private Model model;

    private static final Props props = Configs.load("bottom.qma");

    private static final long BLINK_TIME = props.getInt("qm.blink_time");

    private final ButtonProvider provider = new ButtonProvider(Sector.BOTTOM);

    private final Logic[] tracks = this.provider.group("all_tracks").toArray();
    private final Logic   clear  = this.provider.button("clear");

    @Override
    public void update() {
        List<Path> paths = this.model.paths.getPaths(Sector.BOTTOM);

        if (this.clear.evaluate(this.model)) {
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
            Logic button = this.tracks[i];
            if (button.evaluate(this.model)) {
                this.model.paths.register(path);
                return;
            }
        }
    }

}
