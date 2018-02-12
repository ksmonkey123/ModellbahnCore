package ch.awae.moba.operator;

import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.PathProvider;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;
import ch.awae.utils.logic.Logic;

@Enabled
@Loaded
@Operator("left.AB")
public class Left_AB_Operator implements IOperation {

    private final Logic A_1, B_1, B_2;
    private final Path  A_1_R, B_1_R, B_2_R;

    public Left_AB_Operator() {
        ButtonProvider provider = new ButtonProvider(Sector.LEFT);
        PathProvider pathProvider = PathProvider.getInstance();

        Logic one_pth = provider.group("paths").count(1);
        Logic one_trk = provider.group("tracks").count(1);

        Logic NC = provider.button("clear").not();

        Logic _A = provider.button("path_A").and(one_pth).and(NC);
        Logic _B = provider.button("path_B").and(one_pth).and(NC);
        Logic _1 = provider.button("track_1").and(one_trk).and(NC);
        Logic _2 = provider.button("track_2").and(one_trk).and(NC);

        this.A_1 = _A.and(_1);
        this.B_1 = _B.and(_1);
        this.B_2 = _B.and(_2);

        this.A_1_R = pathProvider.getPath("left.A1_R");
        this.B_1_R = pathProvider.getPath("left.B1_R");
        this.B_2_R = pathProvider.getPath("left.B2_R");
    }

    @Override
    public void update() {
        if (A_1.evaluate())
            A_1_R.issue(true);
        if (B_1.evaluate())
            B_1_R.issue(true);
        if (B_2.evaluate())
            B_2_R.issue(true);
    }

}
