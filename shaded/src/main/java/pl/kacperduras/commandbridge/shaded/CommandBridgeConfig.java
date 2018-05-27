package pl.kacperduras.commandbridge.shaded;

import org.apache.commons.lang3.Validate;
import org.diorite.cfg.annotations.CfgClass;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

@CfgClass(name = "PluginConfig")
@CfgDelegateDefault("{new}")
@CfgComment("CommandBridge v1.0.2 by Kacper Duras")
public class CommandBridgeConfig {

  private Channels channels = new Channels();

  public Channels getChannels() {
    return channels;
  }

  public static class Channels {

    private final Channel bukkit = new Channel("CB-Bukkit", true);
    private final Channel bungeeCord = new Channel("CB-BungeeCord", true);

    public Channel getBukkit() {
      return bukkit;
    }

    public Channel getBungeeCord() {
      return bungeeCord;
    }

    public static class Channel {

      private final String name;
      private final boolean enabled;

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