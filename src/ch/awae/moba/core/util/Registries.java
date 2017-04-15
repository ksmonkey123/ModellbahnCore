package ch.awae.moba.core.util;

import ch.awae.moba.core.operators.AOperator;
import ch.awae.moba.core.threads.IThreaded;

public class Registries {

	public static Registry<IThreaded> threads = new Registry<>();
	public static Registry<AOperator> operators = new Registry<>();

}
