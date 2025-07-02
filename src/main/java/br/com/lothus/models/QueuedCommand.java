package br.com.lothus.models;

public class QueuedCommand {
  public String id;
  public String store_id;
  public String user_id;
  public String command;
  public boolean offline_execute;
  public Order order;

  public QueuedCommand(String id, String store_id, String user_id, String command, boolean offline_execute, Order order) {
    this.id = id;
    this.store_id = store_id;
    this.user_id = user_id;
    this.command = command;
    this.offline_execute = offline_execute;
    this.order = order;
  }

  @Override
  public String toString() {
    return "QueuedCommand{" +
            "id='" + id + '\'' +
            ", store_id='" + store_id + '\'' +
            ", user_id='" + user_id + '\'' +
            ", command='" + command + '\'' +
            ", offline_execute=" + offline_execute +
            ", order=" + order +
            '}';
  }
}
