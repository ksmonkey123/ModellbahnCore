package ch.awae.moba.operator;

import static ch.awae.moba.core.model.Sector.BOTTOM;

import java.util.List;
import java.util.logging.Logger;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;
import ch.awae.moba.core.util.Props;
import ch.awae.moba.core.util.Utils;

@Enabled(true)
@Loaded(true)
@Operator("bottom.supervisor")
public class BottomQuickActivator implements IOperation {

    private static final Props props = Configs.load("bottom.qma");

    private static final long TRIGGER_ON_DELAY       = props.getInt("qm.trigger_on_delay");
    private static final long TRIGGER_OFF_DELAY      = props.getInt("qm.trigger_off_delay");
    private static final long TRANSITION_SLEEP_LONG  = props.getInt("qm.transition_long");
    private static final long TRANSITION_SLEEP_SHORT = props.getInt("qm.transition_short");

    @Operator("bottom.supervisor")
    private IOperator self;

    @Operator("bottom.base")
    private IOperator base;

    @Operator("bottom.qm")
    private IOperator qm;

    private final Logger         logger       = Utils.getLogger();
    private final ButtonProvider provider     = new ButtonProvider(BOTTOM);
    private final PathProvider   pathProvider = PathProvider.getInstance();

    private final Logic clear            = this.provider.button("clear");
    private final Logic enableGroup      = this.provider.group("qm.enable").any();
    private final Logic disableGroup     = this.provider.group("qm.disable").any();
    private final Logic disableFastGroup = this.provider.group("qm.kill").any();

    private final Path[] BOTTOM_LEFT = pathProvider.getPaths("bottom.01", "bottom.02", "bottom.03",
            "bottom.04", "bottom.05", "bottom.06", "bottom.07", "bottom.08", "bottom.09",
            "bottom.10");

    private final Path allGreen = pathProvider.getPath("bottom.system_error");

    private State state = State.BASE;
    private long  timestamp;

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        long deltaT = now - this.timestamp;
        boolean enable = this.enableGroup.evaluate();
        boolean disable = this.disableGroup.evaluate();
        boolean disableNow = this.disableFastGroup.evaluate();

        switch (this.state) {
            case BASE:
                if (enable) {
                    this.state = State.BASE_ARMED;
                    this.timestamp = now;
                }
                break;
            case BASE_ARMED:
                if (!enable)
                    this.state = State.BASE;
                else if (deltaT > TRIGGER_ON_DELAY) {
                    this.state = State.QUICK;
                    Utils.async(() -> enableQM());
                }
                break;
            case QUICK:
                if (disableNow) {
                    this.state = State.BASE;
                    Utils.async(() -> disableQM(true));
                } else if (disable) {
                    this.state = State.QUICK_ARMED;
                    this.timestamp = now;
                }
                break;
            case QUICK_ARMED:
                if (!disable)
                    this.state = State.QUICK;
                else if (deltaT > TRIGGER_OFF_DELAY) {
                    this.state = State.BASE;
                    Utils.async(() -> disableQM(false));
                }
                break;
            default:
                break;
        }
    }

    // TRANSITION METHODS

    private void enableQM() {
        // CACHE POTENTIAL ACTIVE CODE (ONLY <LEFT> IS VALID)
        List<Path> paths = Model.paths().getPaths(BOTTOM);

        Path cached = null;
        for (Path p : BOTTOM_LEFT) {
            if (paths.contains(p)) {
                cached = p;
                break;
            }
        }
        this.logger.info("switching to QickMode");

        // disable BM and supervisor
        this.base.halt();
        this.self.halt();
        // lock controls
        Model.paths().register(allGreen);
        Utils.sleep(TRANSITION_SLEEP_LONG);
        // unlock controls
        Model.paths().unregister(allGreen);
        // restore cached path
        if (cached != null)
            Model.paths().register(cached);
        // enable QM and supervisor
        this.qm.start();
        this.self.start();
    }

    private void disableQM(boolean quick) {
        // CACHE POTENTIAL ACTIVE CODE (ONLY <LEFT> IS VALID)
        List<Path> paths = Model.paths().getPaths(BOTTOM);

        Path cached = null;
        if (!this.clear.evaluate())
            for (Path p : BOTTOM_LEFT)
                if (paths.contains(p)) {
                    cached = p;
                    break;
                }
        this.logger.info("switching to BaseMode");

        // disable QM and supervisor
        this.qm.halt();
        this.self.halt();
        // lock controls
        Model.paths().register(allGreen);
        Utils.sleep(quick ? TRANSITION_SLEEP_SHORT : TRANSITION_SLEEP_LONG);
        // unlock controls
        Model.paths().unregister(allGreen);
        // restore cached path
        if (cached != null)
            Model.paths().register(cached);
        // enable BM and supervisor
        this.base.start();
        this.self.start();

    }

    private static enum State {
        BASE,
        BASE_ARMED,
        QUICK,
        QUICK_ARMED
    }

}
