package ch.awae.moba.operator.right;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Operator;
import ch.awae.moba.core.util.Props;
import ch.awae.utils.logic.Logic;
import ch.awae.utils.logic.LogicGroup;

@Enabled
@Operator("right.B")
public class Right_B_Operator implements IOperation {

    private final static Props props           = Configs.load("station");
    private final static long  DECORATOR_DELAY = props.getInt("decoration_delay");

    private final Logic _B, _1, _2, _3, _4;
    private final Logic _B_solo, _one_trk_solo;

    private final Logic B_1, B_2, B_3, B_4;

    private final Path[] path_B_R, path_B_I, path_B_O;

    private Logic   current;
    private boolean inbound;
    private long    activeTime;
    private int     trackID;
    private boolean processed;

    public Right_B_Operator() {

        ButtonProvider p = new ButtonProvider(Sector.RIGHT);
        PathProvider pathProvider = PathProvider.getInstance();

        LogicGroup paths = p.group("paths");
        LogicGroup tracks = p.group("tracks");

        Logic NC = p.group("clear").none();
        Logic one_pth = paths.count(1);
        Logic one_trk = tracks.count(1);

        this._B = p.button("path_B").and(one_pth).and(NC);
        this._1 = p.button("track_1").and(one_trk).and(NC);
        this._2 = p.button("track_2").and(one_trk).and(NC);
        this._3 = p.button("track_3").and(one_trk).and(NC);
        this._4 = p.button("track_4").and(one_trk).and(NC);

        this._B_solo = this._B.and(tracks.none());
        this._one_trk_solo = one_trk.and(NC.and(paths.none()));

        this.B_1 = this._B.and(this._1);
        this.B_2 = this._B.and(this._2);
        this.B_3 = this._B.and(this._3);
        this.B_4 = this._B.and(this._4);

        path_B_R = pathProvider.getPaths("right.B1_R", "right.B2_R", "right.B3_R", "right.B4_R");
        path_B_I = pathProvider.getPaths("right.B1_I", "right.B2_I", "right.B3_I", "right.B4_I");
        path_B_O = pathProvider.getPaths("right.B1_O", "right.B2_O", "right.B3_O", "right.B4_O");
    }

    @Override
    public void update() {
        Logic curr = this.current;
        if (curr == null) {
            // base mode
            if (this._B_solo.evaluate())
                // only C is pressed => inbound
                this.inbound = true;
            if (this._one_trk_solo.evaluate())
                // exactly one track is pressed => outbound
                this.inbound = false;
            // check for combo
            if (this.B_1.evaluate()) {
                this.trackID = 1;
                this.current = this.B_1;
                this.activeTime = System.currentTimeMillis();
            } else if (this.B_2.evaluate()) {
                this.trackID = 2;
                this.current = this.B_2;
                this.activeTime = System.currentTimeMillis();
            } else if (this.B_3.evaluate()) {
                this.trackID = 3;
                this.current = this.B_3;
                this.activeTime = System.currentTimeMillis();
            } else if (this.B_4.evaluate()) {
                this.trackID = 4;
                this.current = this.B_4;
                this.activeTime = System.currentTimeMillis();
            }
            if (trackID >= 1 && trackID <= 4)
                path_B_R[trackID - 1].issue(true);
        } else {
            // active mode => check if current still holds
            if (curr.evaluate()) {
                // still good => if time reached, decorate
                long deltaT = System.currentTimeMillis() - this.activeTime;
                if ((deltaT > DECORATOR_DELAY) && !this.processed) {
                    this.processed = true;
                    // decorate
                    if (trackID >= 1 && trackID <= 4)
                        (inbound ? path_B_I : path_B_O)[trackID - 1].issue(true);
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
