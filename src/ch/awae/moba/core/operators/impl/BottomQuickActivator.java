package ch.awae.moba.core.operators.impl;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.AOperator;
import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.Utils;

public class BottomQuickActivator extends AOperator {

    private static final long TRIGGER_ON_DELAY       = 2000;
    private static final long TRIGGER_OFF_DELAY      = 1000;
    private static final long TRANSITION_SLEEP_LONG  = 500;
    private static final long TRANSITION_SLEEP_SHORT = 100;

    public BottomQuickActivator() {
        super("bottom (quick mode supervisor)");

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
    public void update(Model model) {
        long now = System.currentTimeMillis();
        long deltaT = now - this.timestamp;
        boolean enable = this.enableGroup.evaluate(model);
        boolean disable = this.disableGroup.evaluate(model);
        boolean disableNow = this.disableFastGroup.evaluate(model);

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
                    Utils.async(() -> enableQM(model));
                }
                break;
            case QUICK:
                if (disableNow) {
                    this.state = State.BASE;
                    Utils.async(() -> disableQM(model, true));
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
                    Utils.async(() -> disableQM(model, false));
                }
                break;
            default:
                break;
        }
    }

    // TRANSITION METHODS

    private void enableQM(Model model) {
        AOperator base_op = Registries.operators
                .find(o -> o.getClass().equals(BottomOperator.class));
        AOperator quic_op = Registries.operators
                .find(o -> o.getClass().equals(BottomQuickMode.class));
        if (quic_op == null) {
            this.logger.warning(
                    "Quick-Mode transition not possible, since QM operator is missing");
            return;
        }
        if (base_op == null) {
            this.logger.severe(
                    "Quick-Mode transition not possible, since Base operator is missing");
            return;
        }
        // CACHE POTENTIAL ACTIVE CODE (ONLY <LEFT> IS VALID)
        List<Path> paths = model.paths.getPaths(Sector.BOTTOM);
        @Nullable
        Path cached = null;
        for (Path p : Path.BOTTOM_LEFT) {
            if (paths.contains(p)) {
                cached = p;
                break;
            }
        }
        this.logger.info("switching to QickMode");

        // disable BM and supervisor
        base_op.halt();
        this.halt();
        // lock controls
        model.paths.register(Path.SYSTEM_ERROR_B);
        Utils.sleep(TRANSITION_SLEEP_LONG);
        // unlock controls
        model.paths.unregister(Path.SYSTEM_ERROR_B);
        // restore cached path
        if (cached != null)
            model.paths.register(cached);
        // enable QM and supervisor
        quic_op.start();
        this.start();
    }

    private void disableQM(Model model, boolean quick) {
        AOperator base_op = Registries.operators
                .find(o -> o.getClass().equals(BottomOperator.class));
        AOperator quic_op = Registries.operators
                .find(o -> o.getClass().equals(BottomQuickMode.class));
        if (quic_op == null) {
            this.logger.severe(
                    "Quick-Mode transition not possible, since QM operator is missing");
            return;
        }
        if (base_op == null) {
            this.logger.severe(
                    "Quick-Mode transition not possible, since Base operator is missing");
            return;
        }
        // CACHE POTENTIAL ACTIVE CODE (ONLY <LEFT> IS VALID)
        List<Path> paths = model.paths.getPaths(Sector.BOTTOM);
        @Nullable
        Path cached = null;
        if (!ButtonMapping.B_CLEAR.evaluate(model))
            for (Path p : Path.BOTTOM_LEFT)
                if (paths.contains(p)) {
                    cached = p;
                    break;
                }
        this.logger.info("switching to BaseMode");

        // disable QM and supervisor
        quic_op.halt();
        this.halt();
        // lock controls
        model.paths.register(Path.SYSTEM_ERROR_B);
        Utils.sleep(quick ? TRANSITION_SLEEP_SHORT : TRANSITION_SLEEP_LONG);
        // unlock controls
        model.paths.unregister(Path.SYSTEM_ERROR_B);
        // restore cached path
        if (cached != null)
            model.paths.register(cached);
        // enable BM and supervisor
        base_op.start();
        this.start();

    }

    private static enum State {
        BASE,
        BASE_ARMED,
        QUICK,
        QUICK_ARMED
    }

}
