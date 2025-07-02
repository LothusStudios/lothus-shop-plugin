package br.com.lothus.interfaces;

import br.com.lothus.models.QueuedCommand;

@FunctionalInterface
public interface CommandProcessor {
  void processCommands(QueuedCommand[] commands);
}