package ch.awae.moba.operator;

import ch.awae.moba.core.Configs;
import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.logic.LogicGroup;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;
import ch.awae.moba.core.util.Props;

@Enabled
@Loaded
@Operator("left.C")
public class Left_C_Operator implements IOperation {

    private final Model model = Model.getInstance();

    private final static Props props           = Configs.load("station");
    private final static long  DECORATOR_DELAY = props.getInt("decoration_delay");

    private final Logic _C, _1, _2, _3, _4;
    private final Logic _C_solo, _one_trk_solo;

    private final Logic C_1, C_2, C_3, C_4;

    private Logic   current;
    private boolean inbound;
    private long    activeTime;
    private int     trackID;
    private boolean processed;

    public Left_C_Operator() {
        ButtonProvider provider = new ButtonProvider(Sector.LEFT);

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
                this.model.paths.register(Path.L_C_1_R);
                this.trackID = 1;
                this.current = this.C_1;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_2.evaluate()) {
                this.model.paths.register(Path.L_C_2_R);
                this.trackID = 2;
                this.current = this.C_2;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_3.evaluate()) {
                this.model.paths.register(Path.L_C_3_R);
                this.trackID = 3;
                this.current = this.C_3;
                this.activeTime = System.currentTimeMillis();
            } else if (this.C_4.evaluate()) {
                this.model.paths.register(Path.L_C_4_R);
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
                    switch (this.trackID) {
                        case 1:
                            this.model.paths.register(this.inbound ? Path.L_C_1_I : Path.L_C_1_O);
                            break;
                        case 2:
                            this.model.paths.register(this.inbound ? Path.L_C_2_I : Path.L_C_2_O);
                            break;
                        case 3:
                            this.model.paths.register(this.inbound ? Path.L_C_3_I : Path.L_C_3_O);
                            break;
                        case 4:
                            this.model.paths.register(this.inbound ? Path.L_C_4_I : Path.L_C_4_O);
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
