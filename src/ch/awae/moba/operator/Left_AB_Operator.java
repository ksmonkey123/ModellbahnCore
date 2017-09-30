package ch.awae.moba.operator;

import ch.awae.moba.core.logic.Logic;
import ch.awae.moba.core.model.ButtonProvider;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.Enabled;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.Loaded;
import ch.awae.moba.core.operators.Operator;

@Enabled
@Loaded
@Operator("left.AB")
public class Left_AB_Operator implements IOperation {

    private final Model model = Model.getInstance();

    private final Logic A_1;
    private final Logic B_1;
    private final Logic B_2;

    public Left_AB_Operator() {
        ButtonProvider provider = new ButtonProvider(Sector.LEFT);

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
    }

    @Override
    public void update() {
        if (this.A_1.evaluate())
            this.model.paths.register(Path.L_A_1_R);
        if (this.B_1.evaluate())
            this.model.paths.register(Path.L_B_1_R);
        if (this.B_2.evaluate())
            this.model.paths.register(Path.L_B_2_R);
    }

}
