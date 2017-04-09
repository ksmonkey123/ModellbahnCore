package ch.awae.moba.core.util;

public class Switch {

	public static enum STATE {

		UNKNOWN(0), PRIMARY(1), SECONDARY(2);

		public int code;

		STATE(int code) {
			this.code = code;
		}
	}

	private STATE state = STATE.UNKNOWN;

	public void setState(STATE state) {
		this.state = state;
	}

	public STATE getState() {
		return this.state;
	}

}
