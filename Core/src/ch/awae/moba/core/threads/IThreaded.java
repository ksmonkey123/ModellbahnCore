package ch.awae.moba.core.threads;

import ch.awae.moba.core.util.Controllable;

public interface IThreaded extends Controllable {

	@Override
    void start();

	void stop() throws InterruptedException;

	@Override
	default void halt() {
		try {
			stop();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
