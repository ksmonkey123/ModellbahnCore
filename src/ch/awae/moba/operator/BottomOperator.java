package ch.awae.moba.operator;

import static ch.awae.moba.core.model.Sector.BOTTOM;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.logic.LogicCluster;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;

@Enabled(true)
@Loaded(true)
@Operator("bottom.base")
public class BottomOperator implements IOperation {

    private final Model model = Model.getInstance();

    private ButtonProvider provider = new ButtonProvider(BOTTOM);

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

    @Override
    public void update() {
        if (clear.evaluate()) {
            model.paths.register(Path.B_CLEAR);
            return;
        }

        Path p = null;
        // @formatter:off
        switch (cluster.evaluate()) {
            case 0x1001: p = Path.B_01_L; break;
            case 0x1002: p = Path.B_02_L; break;
            case 0x1004: p = Path.B_03_L; break;
            case 0x1008: p = Path.B_04_L; break;
            case 0x1010: p = Path.B_05_L; break;
            case 0x1020: p = Path.B_06_L; break;
            case 0x1040: p = Path.B_07_L; break;
            case 0x1080: p = Path.B_08_L; break;
            case 0x1100: p = Path.B_09_L; break;
            case 0x1200: p = Path.B_10_L; break;
            case 0x2002: p = Path.B_02_R; break;
            case 0x2004: p = Path.B_03_R; break;
            case 0x2008: p = Path.B_04_R; break;
            case 0x2010: p = Path.B_05_R; break;
            default: break;
        }
        // @formatter:on

        if (p != null)
            model.paths.register(p);
    }

}
