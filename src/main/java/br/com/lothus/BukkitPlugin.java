package br.com.lothus;

import br.com.lothus.commands.LothusCommand;
import br.com.lothus.config.Properties;
import br.com.lothus.models.*;
import br.com.lothus.utils.Logger;
import com.google.gson.Gson;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class BukkitPlugin extends JavaPlugin {

  private static BukkitPlugin instance;

  private static LothusShop lothusShop;
  private static Gson gson;

  @Override
  public void onLoad() {
    instance = this;
    gson = new Gson();
  }

  @Override
  public void onEnable() {
    instance = this;
    gson = new Gson();

    new Metrics(this, 20185);
    saveDefaultConfig();

    Properties.setup(this);
    registerCommand();

    if (Properties.getSecret().equalsIgnoreCase("token-da-sua-loja")) {
      Logger.info("Parece que esta é a primeira vez que o plugin é carregado. Use /lothus token <seu-token> " +
              "para vincular a loja com o seu servidor");
      return;
    }

    lothusShop = new LothusShop(Properties.getSecret());

    if (!lothusShop.checkToken()) {
      getLogger().info("§c[lothus] Token inválido. Plugin desativado.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    setupLothus();
  }

  @Override
  public void onDisable() {
  }

  private void registerCommand() {
    try {
      final Server server = Bukkit.getServer();
      final Method mapMethod = server.getClass().getMethod("getCommandMap");
      CommandMap bukkitCommandMap = (CommandMap) mapMethod.invoke(server);
      bukkitCommandMap.register(getName(), new LothusCommand());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
      throw new CommandException();
    }
  }

  public void setupLothus() {
    Bukkit.getScheduler().cancelTasks(this);

    lothusShop = new LothusShop(Properties.getSecret());


    new BukkitRunnable() {
      @Override
      public void run() {
        processCommands();
      }
    }.runTaskTimerAsynchronously(this, 20L * 5, 20L * 30);
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
      if (userId == null || userId.trim().isEmpty()) {
        continue;
      }

      Player player = Bukkit.getPlayer(userId);
      if (player == null && !command.offline_execute) {
        continue;
      }

      if (command.command != null && !command.command.trim().isEmpty()) {
        String replaced = command.command.replace("%player%", userId);
        String commandId = command.id;

        Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), () -> {
          try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaced);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      }

      if (command.order != null && command.order.products != null) {
        for (Product product : command.order.products) {
          if (product.commands == null) continue;

          for (String cmd : product.commands) {
            String finalCmd = cmd.replace("%player%", userId);
            String commandId = command.id;

            Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), () -> {
              try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
          }
        }

        if (player != null) {
          Bukkit.getScheduler().runTask(BukkitPlugin.getInstance(), () -> {
            player.sendMessage("");
            player.sendMessage("§b§lCOMPRA REALIZADA!");
            player.sendMessage("§eO seu pedido foi processado com sucesso:");
            for (Product product : command.order.products) {
              player.sendMessage("§f- §7" + product.name);
            }
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.GLASS, 2.0f, 2.0f);
          });
        }
      }

      lothusShop.updateCommandStatus(command.id, "DELIVERED");

    }

  }

  public static BukkitPlugin getInstance() {
    return instance;
  }

}
