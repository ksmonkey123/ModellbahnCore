package ch.awae.moba.core.model.command;

import ch.awae.moba.core.model.Path;

public class Command {

    private Command() {
        throw new AssertionError("unreachable");
    }

    public static UpdateCommand registerPath(Path path) {
        return new PathCommand(path, true);
    }

    public static UpdateCommand unregisterPath(Path path) {
        return new PathCommand(path, false);
    }
    
    

}
