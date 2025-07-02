package br.com.lothus;

import br.com.lothus.commands.LothusCommandBungee;
import br.com.lothus.config.BungeeProperties;
import br.com.lothus.models.Application;
import br.com.lothus.models.Product;
import br.com.lothus.models.QueuedCommand;
import br.com.lothus.utils.Logger;
import br.com.lothus.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BungeePlugin extends Plugin {

  private static BungeePlugin instance;
  private static Configuration config;
  private static LothusShop lothusShop;
  private ScheduledTask commandTask;

  @Override
  public void onEnable() {
    BungeeProperties.setup(this);
    instance = this;
    new Metrics(this, 20185);

    setupConfig();
    getProxy().getPluginManager().registerCommand(this, new LothusCommandBungee());

    if (BungeeProperties.getSecret().equalsIgnoreCase("token-da-sua-loja")) {
      Logger.info("Use /lothus token <seu-token> para vincular sua loja ao servidor.");
    } else {
      setupLothus();
    }
  }

  @Override
  public void onDisable() {
    if (commandTask != null) {
      commandTask.cancel();
    }
  }

  private void setupConfig() {
    if (!getDataFolder().exists()) getDataFolder().mkdir();

    File file = new File(getDataFolder(), "config.yml");
    try {
      if (!file.exists())
        Files.copy(getResourceAsStream("config.yml"), file.toPath());

      config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }



  public void setupLothus() {
    lothusShop = new LothusShop(BungeeProperties.getSecret());

    commandTask = getProxy().getScheduler().schedule(this, this::processCommands, 5, 30, TimeUnit.SECONDS);
  }
  
  public void processCommands() {
    QueuedCommand[] queuedCommands = lothusShop.getPendingCommands();
    if (queuedCommands == null || queuedCommands.length == 0) {
      return;
    }

    Set<String> processed = new HashSet<>();

    for (QueuedCommand command : queuedCommands) {
      if (command == null || command.id == null || processed.contains(command.id)) continue;
      processed.add(command.id);

      String userId = command.user_id;
      if (userId == null || userId.trim().isEmpty()) continue;

      ProxiedPlayer player = ProxyServer.getInstance().getPlayer(userId);
      if (player == null && !command.offline_execute) continue;

      if (command.command != null && !command.command.trim().isEmpty()) {
        String replaced = command.command.replace("%player%", userId);
        try {
          ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), replaced);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      if (command.order != null && command.order.products != null) {
        for (Product product : command.order.products) {
          if (product.commands == null) continue;

          for (String cmd : product.commands) {
            String finalCmd = cmd.replace("%player%", userId);
            try {
              ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), finalCmd);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }

        if (player != null) {
          player.sendMessage(new TextComponent(""));
          player.sendMessage(new TextComponent("§b§lCOMPRA REALIZADA!"));
          player.sendMessage(new TextComponent("§eO seu pedido foi processado com sucesso:"));
          for (Product product : command.order.products) {
            player.sendMessage(new TextComponent("§f- §7" + product.name));
          }
          player.sendMessage(new TextComponent(""));
        }
      }

      lothusShop.updateCommandStatus(command.id, "DELIVERED");
    }
  }


  public static Configuration getConfig() {
    return config;
  }

  public static BungeePlugin getInstance() {
    return instance;
  }


}
