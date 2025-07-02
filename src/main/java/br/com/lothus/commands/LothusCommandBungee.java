package br.com.lothus.commands;

import br.com.lothus.BungeePlugin;
import br.com.lothus.config.BungeeProperties;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LothusCommandBungee extends Command {
  public LothusCommandBungee() {
    super("lothus");
  }

  private void sendHelp(CommandSender sender) {
    sender.sendMessage("");
    sender.sendMessage("§Lothus Shop - Ajuda");
    sender.sendMessage("§a/lothus token <seu-token> §f- Vincula a sua loja com servidor.");
    sender.sendMessage("");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (sender instanceof ProxiedPlayer) {
      sender.sendMessage("Este comando deve ser executado no console.");
      return;
    }

    if (args.length == 0) {
      sendHelp(sender);
      return;
    }

    String command = args[0].toUpperCase();
    switch (command) {
      case "TOKEN":
        if (args.length < 2) {
          sender.sendMessage("§cInforme o token da sua loja.");
          return;
        }

        BungeeProperties.setSecret(args[1]);

        BungeePlugin.getInstance().setupLothus();

        break;

      default:
        sendHelp(sender);
        break;
    }
  }
}
