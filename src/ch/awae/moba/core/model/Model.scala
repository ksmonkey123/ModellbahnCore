package ch.awae.moba.core.model;

import ch.awae.moba.core.model.command.UpdateCommand;

final object Model {
  val paths = new PathRegistry
  val buttons = new Buttons

  private var commandQueue = List.empty[UpdateCommand]

  def issueCommand(cmd: UpdateCommand) = commandQueue ::= cmd
  
  def executeCommands = {
    
  }
  
}