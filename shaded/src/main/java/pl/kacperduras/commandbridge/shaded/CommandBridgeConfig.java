package pl.kacperduras.commandbridge.shaded;

import org.apache.commons.lang3.Validate;

public class CommandBridgeConfig {

  private Channels channels = new Channels();

  public Channels getChannels() {
    return channels;
  }

  public static class Channels {

    private Channel bukkit = new Channel("CommandBridge-Bukkit", true);
    private Channel bungeeCord = new Channel("CommandBridge-BungeeCord", true);

    public Channel getBukkit() {
      return bukkit;
    }

    public Channel getBungeeCord() {
      return bungeeCord;
    }

    public static class Channel {

      private String name;
      private boolean enabled;

      public Channel(String name, boolean enabled) {
        Validate.notNull(name);

        this.name = name;
        this.enabled = enabled;
      }

      public String getName() {
        return name;
      }

      public boolean isEnabled() {
        return enabled;
      }

    }

  }

}
