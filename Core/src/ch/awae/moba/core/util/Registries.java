package ch.awae.moba.core.util;

import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.threads.IThreaded;

public class Registries {

    public static Registry<IThreaded> threads   = new Registry<>();
    public static Registry<IOperator> operators = new Registry<>();

}
