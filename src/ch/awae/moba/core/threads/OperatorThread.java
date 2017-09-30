package ch.awae.moba.core.threads;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.command.PathCommand;
import ch.awae.moba.core.model.command.UpdateCommand;
import ch.awae.moba.core.operators.IOperator;
import ch.awae.moba.core.util.Registries;

public class OperatorThread extends AThreaded {

    final Model model;

    public OperatorThread(Model model) {
        super("operator");
        this.model = model;
    }

    @Override
    protected void step() throws InterruptedException {
        for (String name : Registries.operators.getNames()) {
            IOperator operator = Registries.operators.get(name);
            if (operator != null && operator.isActive())
                operator.update();
            break;
        }

        // apply updates
        // need model lock to block output processor
        synchronized (model) {
            // also need path lock for write access
            synchronized (model.paths) {
                for (UpdateCommand command : model.commandQueue)
                    execute(command);
                model.commandQueue.clear();
            }
        }
    }

    private void execute(UpdateCommand command) {
        // PATH COMMAND
        if (command instanceof PathCommand) {
            PathCommand cmd = (PathCommand) command;
            if (cmd.register)
                model.paths.register(cmd.path);
            else
                model.paths.unregister(cmd.path);
            return;
        }
        // DEFAULT
        throw new IllegalArgumentException("unsupported command: " + command);
    }
}
