package ch.awae.moba.operator;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.logic.LogicGroup;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;
import ch.awae.moba.core.util.Props;

@Enabled
@Loaded
@Operator("right.A")
public class Right_A_Operator implements IOperation {

    private final static Props props           = Configs.load("station");
    private final static long  DECORATOR_DELAY = props.getInt("decoration_delay");

    private final Logic _A, _1, _2, _3, _4;
    private final Logic _A_solo, _one_trk_solo;

    private final Logic A_1, A_2, A_3, A_4;

    private final Path[] path_A_R, path_A_I, path_A_O;

    private Logic   current;
    private boolean inbound;
    private long    activeTime;
    private int     trackID;
    private boolean processed;

    public Right_A_Operator() {
        ButtonProvider p = new ButtonProvider(Sector.RIGHT);
        PathProvider pathProvider = PathProvider.getInstance();

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

        path_A_R = pathProvider.getPaths("right.A1_R", "right.A2_R", "right.A3_R", "right.A4_R");
        path_A_I = pathProvider.getPaths("right.A1_I", "right.A2_I", "right.A3_I", "right.A4_I");
        path_A_O = pathProvider.getPaths("right.A1_O", "right.A2_O", "right.A3_O", "right.A4_O");
    }

    @Override
    public void update() {
        Logic curr = this.current;
        if (curr == null) {
            // base mode
            if (this._A_solo.evaluate())
                // only C is pressed => inbound
                this.inbound = true;
            if (this._one_trk_solo.evaluate())
                // exactly one track is pressed => outbound
                this.inbound = false;
            // check for combo
            if (this.A_1.evaluate()) {
                this.trackID = 1;
                this.current = this.A_1;
                this.activeTime = System.currentTimeMillis();
            } else if (this.A_2.evaluate()) {
                this.trackID = 2;
                this.current = this.A_2;
                this.activeTime = System.currentTimeMillis();
            } else if (this.A_3.evaluate()) {
                this.trackID = 3;
                this.current = this.A_3;
                this.activeTime = System.currentTimeMillis();
            } else if (this.A_4.evaluate()) {
                this.trackID = 4;
                this.current = this.A_4;
                this.activeTime = System.currentTimeMillis();
            }
            if (trackID >= 1 && trackID <= 4)
                path_A_R[trackID - 1].issue(true);

        } else {
            // active mode => check if current still holds
            if (curr.evaluate()) {
                // still good => if time reached, decorate
                long deltaT = System.currentTimeMillis() - this.activeTime;
                if ((deltaT > DECORATOR_DELAY) && !this.processed) {
                    this.processed = true;
                    // decorate
                    if (trackID >= 1 && trackID <= 4)
                        (inbound ? path_A_I : path_A_O)[trackID - 1].issue(true);
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
