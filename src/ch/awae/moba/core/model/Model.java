package ch.awae.moba.core.model;

import java.util.ArrayList;

import ch.awae.moba.core.model.command.UpdateCommand;

public final class Model {

    // SINGLETON
    private static Model INSTANCE = null;

    public static Model getInstance() {
        if (INSTANCE == null)
            synchronized (Model.class) {
                if (INSTANCE == null)
                    INSTANCE = new Model();
            }
        return INSTANCE;
    }

    private Model() {
        super();
    }

    // INSTANCE

    public final PathRegistry paths   = new PathRegistry();
    public final Buttons      buttons = new Buttons();

    public final ArrayList<UpdateCommand> commandQueue = new ArrayList<>();

    public void issueCommand(UpdateCommand cmd) {
        commandQueue.add(cmd);
    }

}
