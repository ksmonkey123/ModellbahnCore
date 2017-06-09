package ch.awae.moba.operator;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.logic.LogicGroup;
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

@Enabled
@Loaded
@Operator("right.A")
public class Right_A_Operator implements IOperation {

    @External
    private Model model;

    private final static Props props           = Configs.load("station");
    private final static long  DECORATOR_DELAY = props.getInt("decoration_delay");

    private final Logic _A, _1, _2, _3, _4;
    private final Logic _A_solo, _one_trk_solo;

    private final Logic A_1, A_2, A_3, A_4;

    private Logic   current;
    private boolean inbound;
    private long    activeTime;
    private int     trackID;
    private boolean processed;

    public Right_A_Operator() {

        ButtonProvider p = new ButtonProvider(Sector.RIGHT);

        LogicGroup paths = p.group("paths");
        LogicGroup tracks = p.group("tracks");

        Logic NC = p.group("clear").none();
        Logic one_pth = paths.count(1);
        Logic one_trk = tracks.count(1);

        this._A = p.button("path_A").and(one_pth).and(NC);
        this._1 = p.button("track_1").and(one_trk).and(NC);
        this._2 = p.button("track_2").and(one_trk).and(NC);
        this._3 = p.button("track_3").and(one_trk).and(NC);
        this._4 = p.button("track_4").and(one_trk).and(NC);

        this._A_solo = this._A.and(tracks.none());
        this._one_trk_solo = one_trk.and(NC.and(paths.none()));

        this.A_1 = this._A.and(this._1);
        this.A_2 = this._A.and(this._2);
        this.A_3 = this._A.and(this._3);
        this.A_4 = this._A.and(this._4);
    }

    @Override
    public void update() {
        Logic curr = this.current;
        if (curr == null) {
            // base mode
            if (this._A_solo.evaluate(this.model))
                // only C is pressed => inbound
                this.inbound = true;
            if (this._one_trk_solo.evaluate(this.model))
                // exactly one track is pressed => outbound
                this.inbound = false;
            // check for combo
            if (this.A_1.evaluate(this.model)) {
                this.model.paths.register(Path.R_A_1_R);
                this.trackID = 1;
                this.current = this.A_1;
                this.activeTime = System.currentTimeMillis();
            } else if (this.A_2.evaluate(this.model)) {
                this.model.paths.register(Path.R_A_2_R);
                this.trackID = 2;
                this.current = this.A_2;
                this.activeTime = System.currentTimeMillis();
            } else if (this.A_3.evaluate(this.model)) {
                this.model.paths.register(Path.R_A_3_R);
                this.trackID = 3;
                this.current = this.A_3;
                this.activeTime = System.currentTimeMillis();
            } else if (this.A_4.evaluate(this.model)) {
                this.model.paths.register(Path.R_A_4_R);
                this.trackID = 4;
                this.current = this.A_4;
                this.activeTime = System.currentTimeMillis();
            }
        } else {
            // active mode => check if current still holds
            if (curr.evaluate(this.model)) {
                // still good => if time reached, decorate
                long deltaT = System.currentTimeMillis() - this.activeTime;
                if ((deltaT > DECORATOR_DELAY) && !this.processed) {
                    this.processed = true;
                    // decorate
                    switch (this.trackID) {
                        case 1:
                            this.model.paths.register(this.inbound ? Path.R_A_1_I : Path.R_A_1_O);
                            break;
                        case 2:
                            this.model.paths.register(this.inbound ? Path.R_A_2_I : Path.R_A_2_O);
                            break;
                        case 3:
                            this.model.paths.register(this.inbound ? Path.R_A_3_I : Path.R_A_3_O);
                            break;
                        case 4:
                            this.model.paths.register(this.inbound ? Path.R_A_4_I : Path.R_A_4_O);
                            break;
                        default:
                            // do nothing
                    }
                }

            } else {
                // degraded => return to base mode
                this.trackID = 0;
                this.current = null;
                this.inbound = false;
                this.processed = false;
            }
        }
    }

}
