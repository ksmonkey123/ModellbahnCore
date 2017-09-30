package ch.awae.moba.core.model.command;

import ch.awae.moba.core.model.Path;

public class PathCommand implements UpdateCommand {

    public final Path    path;
    public final boolean register;

    public PathCommand(Path path, boolean register) {
        this.path = path;
        this.register = register;
    }

}
