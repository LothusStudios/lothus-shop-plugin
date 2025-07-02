package br.com.lothus.utils;

import br.com.lothus.BungeePlugin;

public class Logger {

  private static boolean isBukkit;
  private static boolean isBungee;


  private static void send(String message) {
    Class<?> clazz = null;

    try {
      clazz = Class.forName("net.md_5.bungee");
      isBungee = true;
    } catch (ClassNotFoundException e) {
      isBungee = false;
      //throw new RuntimeException(e);
    }

    if (clazz != null) {
    }else System.out.println(message);
  }

  public static void info(String message) {
    send("§a[Lothus API] => §r" + message);
  }

  public static void error(String message) {
   send("§c[Lothus API] => §4" + message);
  }

  public static void debug(String message) {
    if (isBungee) {
      if (BungeePlugin.getConfig().getBoolean("debug_mode"))
        send("§e[Lothus API] => §r" + message);
      }
    }
}
