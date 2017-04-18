package operator;

import java.util.List;
import java.util.logging.Logger;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;
import ch.awae.moba.core.util.Utils;

@Enabled(true)
@Loaded(true)
@Operator("bottom.supervisor")
public class BottomQuickActivator implements IOperation {

    @External
    private Model model;

    @Operator("bottom.supervisor")
    private IOperator self;

    @Operator("bottom.base")
    private IOperator base;

    @Operator("bottom.qm")
    private IOperator qm;

    private static final long TRIGGER_ON_DELAY       = 2000;
    private static final long TRIGGER_OFF_DELAY      = 1000;
    private static final long TRANSITION_SLEEP_LONG  = 500;
    private static final long TRANSITION_SLEEP_SHORT = 100;

    public BottomQuickActivator() {
        this.enableGroup = ButtonMapping.B_ENT_L;
        this.disableGroup = ButtonMapping.B_CLEAR.or(ButtonMapping.B_ENT_L);
        this.disableFastGroup = ButtonMapping.B_ENT_R;
    }

    private final Logger logger = Utils.getLogger();
    private final Logic  disableGroup;
    private final Logic  enableGroup;
    private final Logic  disableFastGroup;

    private State state = State.BASE;
    private long  timestamp;

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        long deltaT = now - this.timestamp;
        boolean enable = this.enableGroup.evaluate(this.model);
        boolean disable = this.disableGroup.evaluate(this.model);
        boolean disableNow = this.disableFastGroup.evaluate(this.model);

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
        List<Path> paths = this.model.paths.getPaths(Sector.BOTTOM);

        Path cached = null;
        for (Path p : Path.BOTTOM_LEFT) {
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
        this.model.paths.register(Path.SYSTEM_ERROR_B);
        Utils.sleep(TRANSITION_SLEEP_LONG);
        // unlock controls
        this.model.paths.unregister(Path.SYSTEM_ERROR_B);
        // restore cached path
        if (cached != null)
            this.model.paths.register(cached);
        // enable QM and supervisor
        this.qm.start();
        this.self.start();
    }

    private void disableQM(boolean quick) {
        // CACHE POTENTIAL ACTIVE CODE (ONLY <LEFT> IS VALID)
        List<Path> paths = this.model.paths.getPaths(Sector.BOTTOM);

        Path cached = null;
        if (!ButtonMapping.B_CLEAR.evaluate(this.model))
            for (Path p : Path.BOTTOM_LEFT)
                if (paths.contains(p)) {
                    cached = p;
                    break;
                }
        this.logger.info("switching to BaseMode");

        // disable QM and supervisor
        this.qm.halt();
        this.self.halt();
        // lock controls
        this.model.paths.register(Path.SYSTEM_ERROR_B);
        Utils.sleep(quick ? TRANSITION_SLEEP_SHORT : TRANSITION_SLEEP_LONG);
        // unlock controls
        this.model.paths.unregister(Path.SYSTEM_ERROR_B);
        // restore cached path
        if (cached != null)
            this.model.paths.register(cached);
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
