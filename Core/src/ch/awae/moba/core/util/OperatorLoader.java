package ch.awae.moba.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.operators.IOperation;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.operators.PureOperator;
import ch.awae.moba.core.operators.annotations.Enabled;
import ch.awae.moba.core.operators.annotations.External;
import ch.awae.moba.core.operators.annotations.Loaded;
import ch.awae.moba.core.operators.annotations.Operator;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class OperatorLoader {

    public static void loadOperators(Model model) throws IllegalAccessException, RuntimeException {
        OperatorLoader loader = new OperatorLoader(model);
        loader.load();
    }

    private final Model      model;
    private volatile boolean consumed = false;

    private OperatorLoader(Model model) {
        this.model = model;
    }

    private final Logger logger = Utils.getLogger();

    private synchronized void load() throws IllegalAccessException, RuntimeException {
        if (this.consumed)
            throw new IllegalArgumentException("instance already consumed");
        this.consumed = true;
        new FastClasspathScanner().matchClassesWithAnnotation(Operator.class, this::process).scan();
        this.bind();
        for (Entry<String, PureOperator> entry : this.opMap.entrySet()) {
            Registries.operators.register(entry.getKey(), entry.getValue());
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
            this.logger.info("checking class " + clazz.getName());

            Enabled enabled = instClass.getAnnotation(Enabled.class);

            Loaded loaded = instClass.getAnnotation(Loaded.class);
            Operator operator = instClass.getAnnotation(Operator.class);
            String name = operator.value();
            if (loaded != null && !loaded.value()) {
                this.logger.info("class " + instClass.getName() + " will not be loaded!");
            }

            try {
                IOperation inst = instClass.getConstructor().newInstance();
                assert inst != null;
                PureOperator op = new PureOperator(name, inst);
                if (enabled != null && enabled.value()) {
                    this.logger.info("enabling op '" + name + "'");
                    op.start();
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
        for (IOperation op : this.operations.values())
            this.bind(op);
    }

    private void bind(IOperation operation) throws IllegalAccessException, RuntimeException {
        this.logger.info("binding " + operation.getClass().getName());
        final Class<? extends IOperation> clazz = operation.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(External.class) != null)
                this.bindExternal(operation, field);
            Operator op = field.getAnnotation(Operator.class);
            if (op != null)
                this.bindOperator(operation, field, op.value());
        }
    }

    private void bindOperator(IOperation op, Field field, String value)
            throws IllegalArgumentException, IllegalAccessException {
        String fieldName = op.getClass() + "#" + field.getName() + ":" + field.getType().getName();
        if (!field.isAccessible())
            field.setAccessible(true);
        if (field.getType().isAssignableFrom(IOperator.class)) {
            if (this.opMap.containsKey(value)) {
                this.logger.info("binding operator " + value + " to field " + fieldName);
                field.set(op, this.opMap.get(value));
            } else {
                throw new IllegalArgumentException("no operator available for name " + value);
            }

        }
    }

    private void bindExternal(IOperation op, Field field)
            throws RuntimeException, IllegalAccessException {
        String fieldName = op.getClass() + "#" + field.getName() + ":" + field.getType().getName();
        if (!field.isAccessible())
            field.setAccessible(true);
        if (field.getType().equals(Model.class)) {
            this.logger.info("binding model to field " + fieldName);
            field.set(op, this.model);
        } else {
            throw new IllegalArgumentException("no binding available for field " + fieldName);
        }
    }

}
