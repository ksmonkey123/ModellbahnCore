package ch.awae.moba.core.model;

import java.util.ArrayList;

import ch.awae.moba.core.model.command.UpdateCommand;

public class Model {

    public final PathRegistry paths   = new PathRegistry();
    public final Buttons      buttons = new Buttons();

    public final ArrayList<UpdateCommand> commandQueue = new ArrayList<>();

    public void issueCommand(UpdateCommand cmd) {
        commandQueue.add(cmd);
    }

}
