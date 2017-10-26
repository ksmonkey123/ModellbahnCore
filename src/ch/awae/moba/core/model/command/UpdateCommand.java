package ch.awae.moba.core.model.command;

import ch.awae.moba.core.model.Model;

public interface UpdateCommand {
    // marker interface

    default void issue() {
        Model.issueCommand(this);
    }

    default void issueNow() {
        Model.issueCommand(this);
        Model.executeCommands();
    }

}
