package ch.awae.moba.core.operators;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.util.BatchLog;
import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.Utils;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class OperatorLoader {

    public static void loadOperators() throws IllegalAccessException, RuntimeException {
        OperatorLoader loader = new OperatorLoader();
        loader.load();
    }

    private volatile boolean consumed = false;

    private OperatorLoader() {
        super();
    }

    private final Logger logger = Utils.getLogger();
    private BatchLog     log    = new BatchLog(logger, Level.INFO, "load operators");

    private synchronized void load() throws IllegalAccessException, RuntimeException {
        try {
            if (this.consumed)
                throw new IllegalArgumentException("instance already consumed");
            this.consumed = true;
            logger.info("starting class path scan ...");
            new FastClasspathScanner().matchClassesWithAnnotation(Operator.class, this::process)
                    .scan();
            logger.info("scan complete");
            this.bind();
            for (Entry<String, PureOperator> entry : this.opMap.entrySet()) {
                Registries.operators.register(entry.getKey(), entry.getValue());
            }
        } finally {
            log.flush();
        }
    }

    private final Map<String, PureOperator> opMap      = new HashMap<>();
    private final Map<String, IOperation>   operations = new HashMap<>();

    private void process(Class<?> clazz1) {
        if (clazz1 == null)
            return;
        try {
            Class<?> clazz = Class.forName(clazz1.getName());
            assert clazz != null;
            if (!IOperation.class.isAssignableFrom(clazz))
                return;
            @SuppressWarnings("unchecked")
            Class<? extends IOperation> instClass = (Class<? extends IOperation>) clazz;
            this.logger.fine("checking class " + clazz.getName());

            Enabled enabled = instClass.getAnnotation(Enabled.class);
            Loaded loaded = instClass.getAnnotation(Loaded.class);
            Operator operator = instClass.getAnnotation(Operator.class);

            String name = operator.value();
            if (loaded != null && !loaded.value()) {
                log.log("skip   ' x " + name + "'");
                return;
            }

            try {
                IOperation inst = instClass.getConstructor().newInstance();
                assert inst != null;
                PureOperator op = new PureOperator(name, inst);
                if (enabled != null && enabled.value()) {
                    this.logger.fine("enabling op '" + name + "'");
                    log.log("loaded ' * " + name + "'");
                    op.start();
                } else {
                    log.log("loaded '   " + name + "'");
                }
                this.opMap.put(name, op);
                this.operations.put(name, inst);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                this.logger.log(Level.SEVERE, "failed constructor " + operator.value(), e);
            }
        } catch (ClassNotFoundException e1) {
            throw new RuntimeException(e1);
        }

    }

    /**
     * binds external resource references
     * 
     * @throws RuntimeException
     * @throws IllegalAccessException
     */
    private void bind() throws IllegalAccessException, RuntimeException {
        for (String op : this.operations.keySet())
            this.bind(op);
    }

    private void bind(String name) throws IllegalAccessException, RuntimeException {
        IOperation operation = operations.get(name);
        final Class<? extends IOperation> clazz = operation.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Operator op = field.getAnnotation(Operator.class);
            if (op != null)
                this.bindOperator(name, operation, field, op.value());
        }
    }

    private void bindOperator(String name, IOperation op, Field field, String value)
            throws IllegalArgumentException, IllegalAccessException {
        String fieldName = op.getClass() + "#" + field.getName() + ":" + field.getType().getName();
        if (!field.isAccessible())
            field.setAccessible(true);
        if (field.getType().isAssignableFrom(IOperator.class)) {
            if (this.opMap.containsKey(value)) {
                this.logger.fine("binding operator " + value + " to field " + fieldName);
                log.log("'" + name + "':" + field.getName() + " <- '" + value + "'");
                field.set(op, this.opMap.get(value));
            } else {
                throw new IllegalArgumentException(
                        "no operator available for name '" + value + "'");
            }

        }
    }

}
