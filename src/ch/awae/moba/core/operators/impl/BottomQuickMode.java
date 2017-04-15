package ch.awae.moba.core.operators.impl;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.AOperator;
import ch.awae.moba.core.operators.annotations.Enabled;

@Enabled(false)
public class BottomQuickMode extends AOperator {

    private static final long BLINK_TIME = 500;

    public BottomQuickMode() {
        super("bottom (quick mode)");
    }

    @Override
    public void update(@NonNull Model model) {
        List<Path> paths = model.paths.getPaths(Sector.BOTTOM);

        if (ButtonMapping.B_CLEAR.evaluate(model)) {
            for (Path path : model.paths.getPaths(Sector.BOTTOM)) {
                if (path != Path.B_DUMMY_L && !path.forced)
                    model.paths.unregister(path);
            }
        }

        if (paths.isEmpty() || (paths.size() == 1) && paths.contains(Path.B_DUMMY_L)) {
            if (System.currentTimeMillis() % (2 * BLINK_TIME) > BLINK_TIME)
                model.paths.register(Path.B_DUMMY_L);
            else
                model.paths.unregister(Path.B_DUMMY_L);
        }
        for (int i = 0; i < 10; i++) {
            Path path = Path.BOTTOM_LEFT[i];
            assert path != null;
            ButtonMapping button = ButtonMapping.BOTTOM_TRACKS[i];
            if (button.evaluate(model)) {
                model.paths.register(path);
                return;
            }
        }
    }

}
