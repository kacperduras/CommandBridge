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

    private Channel bukkit = new Channel("CB-Bukkit", true);
    private Channel bungeeCord = new Channel("CB-BungeeCord", true);

    public Channel getBukkit() {
      return bukkit;
    }

    public Channel getBungeeCord() {
      return bungeeCord;
    }

    public static class Channel {

      private String name;
      private boolean enabled;

      public Channel() {
      }

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
