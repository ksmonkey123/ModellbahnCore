package ch.awae.moba.operator.left;

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
@Operator("left.C")
public class Left_C_Operator implements IOperation {

    private final static Props props           = Configs.load("station");
    private final static long  DECORATOR_DELAY = props.getInt("decoration_delay");

    private final Logic _C, _1, _2, _3, _4;
    private final Logic _C_solo, _one_trk_solo;

    private final Logic C_1, C_2, C_3, C_4;

    private final Path[] path_R, path_I, path_O;

    private Logic   current;
    private boolean inbound;
    private long    activeTime;
    private int     trackID;
    private boolean processed;

    public Left_C_Operator() {
        ButtonProvider provider = new ButtonProvider(Sector.LEFT);
        PathProvider pathProvider = PathProvider.getInstance();

        Logic NC = provider.button("clear").not();

        LogicGroup paths = provider.group("paths");
        LogicGroup tracks = provider.group("tracks");
        Logic one_pth = paths.count(1);
        Logic one_trk = tracks.count(1);

        this._C = provider.button("path_C").and(one_pth).and(NC);
        this._1 = provider.button("track_1").and(one_trk).and(NC);
        this._2 = provider.button("track_2").and(one_trk).and(NC);
        this._3 = provider.button("track_3").and(one_trk).and(NC);
        this._4 = provider.button("track_4").and(one_trk).and(NC);

        this._C_solo = this._C.and(tracks.none());
        this._one_trk_solo = one_trk.and(paths.none());

        this.C_1 = this._C.and(this._1);
        this.C_2 = this._C.and(this._2);
        this.C_3 = this._C.and(this._3);
        this.C_4 = this._C.and(this._4);

        this.path_R = pathProvider.getPaths("left.C1_R", "left.C2_R", "left.C3_R", "left.C4_R");
        this.path_I = pathProvider.getPaths("left.C1_I", "left.C2_I", "left.C3_I", "left.C4_I");
        this.path_O = pathProvider.getPaths("left.C1_O", "left.C2_O", "left.C3_O", "left.C4_O");
    }

    @Override
    public void update() {
        Logic curr = this.current;
        if (curr == null) {
            // base mode
            if (this._C_solo.evaluate())
                // only C is pressed => inbound
                this.inbound = true;
            if (this._one_trk_solo.evaluate())
                // exactly one track is pressed => outbound
                this.inbound = false;
            // check for combo
            if (this.C_1.evaluate()) {
                path_R[0].issue(true);
                this.trackID = 1;
                this.current = this.C_1;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_2.evaluate()) {
                path_R[1].issue(true);
                this.trackID = 2;
                this.current = this.C_2;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_3.evaluate()) {
                path_R[2].issue(true);
                this.trackID = 3;
                this.current = this.C_3;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_4.evaluate()) {
                path_R[3].issue(true);
                this.trackID = 4;
                this.current = this.C_4;
                this.activeTime = System.currentTimeMillis();
            }
        } else {
            // active mode => check if current still holds
            if (curr.evaluate()) {
                // still good => if time reached, decorate
                long deltaT = System.currentTimeMillis() - this.activeTime;
                if ((deltaT > DECORATOR_DELAY) && !this.processed) {
                    this.processed = true;
                    // decorate
                    if (trackID >= 1 && trackID <= 4)
                        (inbound ? path_I : path_O)[trackID - 1].issue(true);
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
