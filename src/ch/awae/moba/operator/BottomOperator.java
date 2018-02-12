package ch.awae.moba.operator;

import static ch.awae.moba.core.model.Sector.BOTTOM;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;
import ch.awae.utils.logic.LogicCluster;

@Enabled(true)
@Loaded(true)
@Operator("bottom.base")
public class BottomOperator implements IOperation {

    private ButtonProvider provider     = new ButtonProvider(BOTTOM);
    private PathProvider   pathProvider = PathProvider.getInstance();

    private Logic clear = provider.button("clear");

    private LogicCluster cluster = Logic.cluster(//
            provider.button("track_01"), //
            provider.button("track_02"), //
            provider.button("track_03"), //
            provider.button("track_04"), //
            provider.button("track_05"), //
            provider.button("track_06"), //
            provider.button("track_07"), //
            provider.button("track_08"), //
            provider.button("track_09"), //
            provider.button("track_10"), //
            null, //
            null, //
            provider.button("exit_left"), //
            provider.button("exit_right"));

    private Path[] leftpaths  = pathProvider.getPaths("bottom.01", "bottom.02", "bottom.03",
            "bottom.04", "bottom.05", "bottom.06", "bottom.07", "bottom.08", "bottom.09",
            "bottom.10");
    private Path[] rightpaths = pathProvider.getPaths("bottom.r2", "bottom.r3", "bottom.r4",
            "bottom.r5");
    private Path   clearPath  = pathProvider.getPath("bottom.clear");

    @Override
    public void update() {
        if (clear.evaluate()) {
            clearPath.issue(true);
            return;
        }

        Path p = null;
        // @formatter:off
        switch (cluster.evaluate()) {
            case 0x1001: p = leftpaths[0]; break;
            case 0x1002: p = leftpaths[1]; break;
            case 0x1004: p = leftpaths[2]; break;
            case 0x1008: p = leftpaths[3]; break;
            case 0x1010: p = leftpaths[4]; break;
            case 0x1020: p = leftpaths[5]; break;
            case 0x1040: p = leftpaths[6]; break;
            case 0x1080: p = leftpaths[7]; break;
            case 0x1100: p = leftpaths[8]; break;
            case 0x1200: p = leftpaths[9]; break;
            case 0x2002: p = rightpaths[0]; break;
            case 0x2004: p = rightpaths[1]; break;
            case 0x2008: p = rightpaths[2]; break;
            case 0x2010: p = rightpaths[3]; break;
            default: break;
        }
        // @formatter:on

        if (p != null)
            p.issue(true);
    }

}
