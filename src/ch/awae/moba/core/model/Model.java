package ch.awae.moba.core.model;

import ch.awae.moba.core.model.command.UpdateCommand;

final public class Model {
    private final static PathRegistry paths   = new PathRegistry();
    private final static Buttons      buttons = new Buttons();

    public static void issueCommand(UpdateCommand command) {
        // TODO: implement
    }

    public static void executeCommands() {
        // TODO: implement
    }

    public static Buttons buttons() {
        return buttons;
    }

    public static PathRegistry paths() {
        return paths;
    }

}
