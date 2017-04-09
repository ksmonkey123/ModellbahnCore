package ch.awae.moba.core.threads;

public interface IThreaded {

	void start();

	void stop() throws InterruptedException;

}
