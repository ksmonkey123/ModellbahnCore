package ch.awae.moba.core.model;

import java.util.LinkedList;
import java.util.List;

import ch.awae.moba.core.model.command.PathCommand;
import ch.awae.moba.core.model.command.UpdateCommand;

final public class Model {
    private final static PathRegistry paths   = new PathRegistry();
    private final static Buttons      buttons = new Buttons();
    private final static Lights       lights  = new Lights();

    private final static LinkedList<UpdateCommand> queue = new LinkedList<>();

    public static void issueCommand(UpdateCommand command) {
        synchronized (queue) {
            queue.add(command);
        }
    }

    public static void executeCommands() {
        synchronized (queue) {
            for (UpdateCommand command : queue) {
                if (command instanceof PathCommand)
                    executeCommand((PathCommand) command);
            }
            queue.clear();
        }
    }

    public static Buttons buttons() {
        return buttons;
    }

    public static Lights lights() {
        return lights;
    }

    public static List<Path> getActivePaths() {
        return paths.getAllPaths();
    }

    public static List<Path> getActivePaths(Sector sector) {
        return paths.getPaths(sector);
    }

    public static boolean isPathActive(Path path) {
        return paths.getAllPaths().contains(path);
    }

    // COMMAND EXECUTORS

    private static void executeCommand(PathCommand cmd) {
        if (cmd.register)
            paths.register(cmd.path);
        else
            paths.unregister(cmd.path);
    }

}
