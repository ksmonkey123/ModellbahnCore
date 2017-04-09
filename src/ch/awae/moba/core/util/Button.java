package ch.awae.moba.core.util;

public final class Button {

	private volatile boolean state;
	private volatile boolean edge;

	public void setState(boolean state) {
		this.edge = state != this.state;
		this.state = state;
	}

	public boolean isPressed() {
		return this.state;
	}

	public boolean isReleased() {
		return !this.state;
	}

	public boolean isRising() {
		return this.state && this.edge;
	}

	public boolean isFalling() {
		return (!this.state) & this.edge;
	}

}
