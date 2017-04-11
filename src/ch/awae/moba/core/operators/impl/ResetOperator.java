package ch.awae.moba.core.operators.impl;

import static ch.awae.moba.core.model.ButtonMapping.B_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.C_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.L_CLEAR;
import static ch.awae.moba.core.model.ButtonMapping.R_CLR_A;
import static ch.awae.moba.core.model.ButtonMapping.R_CLR_B;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.logic.Logic;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.util.Utils;

public class ResetOperator extends IOperator {

	private final static long ARM_TIME = 5000;

	private final Logic trigger;
	private boolean armed;
	private long armTime;

	public ResetOperator() {
		super("system reset");

		trigger = Logic.count(3, B_CLEAR, L_CLEAR, C_CLEAR, R_CLR_A, R_CLR_B);
	}

	@Override
	public void update(Model model) {
		boolean signal = trigger.evaluate(model);

		if (signal) {
			if (armed) {
				long deltaT = System.currentTimeMillis() - armTime;
				if (deltaT > ARM_TIME) {
					Utils.doReboot(model);
				}
			} else {
				armed = true;
				armTime = System.currentTimeMillis();
			}
		} else {
			armed = false;
		}

	}

}
