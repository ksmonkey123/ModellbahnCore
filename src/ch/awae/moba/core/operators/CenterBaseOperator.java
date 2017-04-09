package ch.awae.moba.core.operators;

import org.eclipse.jdt.annotation.NonNull;

import ch.awae.moba.core.model.CenterBlock;
import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.util.Switch.STATE;

public class CenterBaseOperator implements IOperator {

	@Override
	public void update(@NonNull Model model) {
		CenterBlock block = model.centerBlock;

		if (block.button_reset.isPressed()) {
			block.switch_1.setState(STATE.UNKNOWN);
			block.switch_2.setState(STATE.UNKNOWN);
			block.switch_3.setState(STATE.UNKNOWN);
			block.switch_4.setState(STATE.UNKNOWN);
			return;
		}

		if (block.button_1a.isPressed()) {
			if (block.button_1b.isReleased())
				block.switch_1.setState(STATE.PRIMARY);
		} else {
			if (block.button_1b.isPressed())
				block.switch_1.setState(STATE.SECONDARY);
		}
		if (block.button_2a.isPressed()) {
			if (block.button_2b.isReleased())
				block.switch_2.setState(STATE.PRIMARY);
		} else {
			if (block.button_2b.isPressed())
				block.switch_2.setState(STATE.SECONDARY);
		}
		if (block.button_3a.isPressed()) {
			if (block.button_3b.isReleased())
				block.switch_3.setState(STATE.PRIMARY);
		} else {
			if (block.button_3b.isPressed())
				block.switch_3.setState(STATE.SECONDARY);
		}
		if (block.button_4a.isPressed()) {
			if (block.button_4b.isReleased())
				block.switch_4.setState(STATE.PRIMARY);
		} else {
			if (block.button_4b.isPressed())
				block.switch_4.setState(STATE.SECONDARY);
		}
	}

}
