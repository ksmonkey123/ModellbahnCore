package ch.awae.moba.operator;

import ch.awae.moba.core.model.ButtonMapping;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.model.Sector;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;

@Enabled(true)
@Loaded(true)
@Operator("bottom.base")
public class BottomOperator implements IOperation {

    @External
    private Model model;

    @Override
    public void update() {
        if (this.model.buttons.getState(ButtonMapping.B_CLEAR)) {
            this.model.paths.register(Path.B_CLEAR);
            return;
        }

        short buttons = (short) (this.model.buttons.getState(Sector.BOTTOM) & 0x00000fff);

        Path p = null;
        switch (buttons) {
            case 0x0401:
                p = Path.B_01_L;
                break;
            case 0x0402:
                p = Path.B_02_L;
                break;
            case 0x0404:
                p = Path.B_03_L;
                break;
            case 0x0408:
                p = Path.B_04_L;
                break;
            case 0x0410:
                p = Path.B_05_L;
                break;
            case 0x0420:
                p = Path.B_06_L;
                break;
            case 0x0440:
                p = Path.B_07_L;
                break;
            case 0x0480:
                p = Path.B_08_L;
                break;
            case 0x0500:
                p = Path.B_09_L;
                break;
            case 0x0600:
                p = Path.B_10_L;
                break;
            case 0x0802:
                p = Path.B_02_R;
                break;
            case 0x0804:
                p = Path.B_03_R;
                break;
            case 0x0808:
                p = Path.B_04_R;
                break;
            case 0x0810:
                p = Path.B_05_R;
                break;
            default:
                break;
        }
        if (p != null)
            this.model.paths.register(p);
    }

}