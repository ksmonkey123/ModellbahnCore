package ch.awae.moba.operator;

import ch.awae.moba.core.model.Sector.BOTTOM

import java.util.List
import java.util.logging.Logger

import ch.awae.moba.core.Configs
import ch.awae.moba.core.logic.Logic
import ch.awae.moba.core.model.ButtonProvider
import ch.awae.moba.core.model.Model
import ch.awae.moba.core.model.Path
import ch.awae.moba.core.operators.Enabled
import ch.awae.moba.core.operators.External
import ch.awae.moba.core.operators.IOperation
import ch.awae.moba.core.operators.IOperator
import ch.awae.moba.core.operators.Loaded
import ch.awae.moba.core.operators.Operator
import ch.awae.moba.core.util.Props
import ch.awae.moba.core.util.Utils
import ch.awae.moba.operator.BottomQuickActivator.State.BASE

@Enabled(true)
@Loaded(true)
@Operator("bottom.supervisor")
class BottomQuickActivator extends IOperation {

    private val props = Configs.load("bottom.qma");

    private val TRIGGER_ON_DELAY       = props.getInt("qm.trigger_on_delay");
    private val TRIGGER_OFF_DELAY      = props.getInt("qm.trigger_off_delay");
    private val TRANSITION_SLEEP_LONG  = props.getInt("qm.transition_long");
    private val TRANSITION_SLEEP_SHORT = props.getInt("qm.transition_short");

    @External
    private val model : Model = null

    @Operator("bottom.supervisor")
    private val self : IOperator = null

    @Operator("bottom.base")
    private val base : IOperator = null

    @Operator("bottom.qm")
    private val qm : IOperator = null

    private val        logger   = Utils.getLogger();
    private val provider = new ButtonProvider(BOTTOM);

    private val clear            = provider.button("clear");
    private val enableGroup      = provider.group("qm.enable").any();
    private val disableGroup     = provider.group("qm.disable").any();
    private val disableFastGroup = provider.group("qm.kill").any();

    private var state : State = State.BASE
    private var timestamp : Long = 0

    override def update {
      import State._
        val now = System.currentTimeMillis();
        val deltaT = now - timestamp;
        val enable = enableGroup.evaluate(model);
        val disable = disableGroup.evaluate(model);
        val disableNow = disableFastGroup.evaluate(model);

        state match {
            case BASE =>
                if (enable) {
                    state = BASE_ARMED;
                    timestamp = now;
                }
            case BASE_ARMED => 
                if (!enable)
                    state = BASE;
                else if (deltaT > TRIGGER_ON_DELAY) {
                    state = State.QUICK;
                    Utils.async(enableQM());
                }
                break;
            case QUICK:
                if (disableNow) {
                    state = State.BASE;
                    Utils.async(() -> disableQM(true));
                } else if (disable) {
                    state = State.QUICK_ARMED;
                    timestamp = now;
                }
                break;
            case QUICK_ARMED:
                if (!disable)
                    state = State.QUICK;
                else if (deltaT > TRIGGER_OFF_DELAY) {
                    state = State.BASE;
                    Utils.async(() -> disableQM(false));
                }
                break;
            default:
                break;
        }
    }

    // TRANSITION METHODS

    def enableQM() {
        // CACHE POTENTIAL ACTIVE CODE (ONLY <LEFT> IS VALID)
        val paths = model.getPaths(BOTTOM);

        var cached : Path = null;
        for (p <- Path.BOTTOM_LEFT) {
            if (paths.contains(p)) {
                cached = p;
            }
        }
        logger.info("switching to QickMode");

        // disable BM and supervisor
        base.halt();
        self.halt();
        // lock controls
        model.register(Path.SYSTEM_ERROR_B);
        Utils.sleep(TRANSITION_SLEEP_LONG);
        // unlock controls
        model.unregister(Path.SYSTEM_ERROR_B);
        // restore cached path
        if (cached != null)
            model.register(cached);
        // enable QM and supervisor
        qm.start();
        self.start();
    }

    def disableQM(quick: Boolean) {
        // CACHE POTENTIAL ACTIVE CODE (ONLY <LEFT> IS VALID)
        val paths = model.getPaths(BOTTOM);

        var cached :Path = null;
        if (!clear.evaluate(model))
            for (p <- Path.BOTTOM_LEFT)
                if (paths.contains(p)) {
                    cached = p;
                }
        logger.info("switching to BaseMode");

        // disable QM and supervisor
        qm.halt();
        self.halt();
        // lock controls
        model.register(Path.SYSTEM_ERROR_B);
        Utils.sleep(if (quick) TRANSITION_SLEEP_SHORT else TRANSITION_SLEEP_LONG);
        // unlock controls
        model.unregister(Path.SYSTEM_ERROR_B);
        // restore cached path
        if (cached != null)
            model.register(cached);
        // enable BM and supervisor
        base.start();
        self.start();
    }

    private trait State {}
    private object State {
      case object BASE extends State
      case object BASE_ARMED extends State
      case object QUICK extends State
      case object  QUICK_ARMED extends State
    }

}
