package pl.kacperduras.commandbridge.bukkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import pl.kacperduras.commandbridge.shaded.CommandBridgeAPI;
import pl.kacperduras.commandbridge.shaded.CommandBridgeConfig;
import pl.kacperduras.commandbridge.shaded.util.ConfigUtils;

public final class CommandBridgeBukkit extends JavaPlugin implements CommandBridgeAPI,
    PluginMessageListener {

  private CommandBridgeConfig config;
  
  @Override
  public void onEnable() {
    this.config = ConfigUtils.loadConfig(
        new File(this.getDataFolder(), "config.yml"), CommandBridgeConfig.class);

    if (this.config.getChannels().getBukkit().isEnabled()) {
      this.getServer().getMessenger().registerIncomingPluginChannel(
          this, this.config.getChannels().getBukkit().getName(), this
      );
    }

    if (this.config.getChannels().getBungeeCord().isEnabled()) {
      this.getServer().getMessenger().registerOutgoingPluginChannel(
          this, this.config.getChannels().getBungeeCord().getName()
      );
    }
  }

  @Override
  public void onDisable() {
    if (this.config.getChannels().getBukkit().isEnabled()) {
      this.getServer().getMessenger().unregisterIncomingPluginChannel(
          this, this.config.getChannels().getBukkit().getName()
      );
    }

    if (this.config.getChannels().getBungeeCord().isEnabled()) {
      this.getServer().getMessenger().unregisterOutgoingPluginChannel(
          this, this.config.getChannels().getBungeeCord().getName()
      );
    }
  }

  @Override
  public void bukkit(String nickname, String server, String command) {
    throw new RuntimeException("This platform doesn't support sending messages to Bungee!");
  }

  @Override
  public void bungee(String nickname, String command) {
    Validate.notNull(nickname);
    Validate.notNull(command);

    Player target = this.getServer().getPlayer(nickname);
    if (target == null) {
      return;
    }

    if (this.config.getChannels().getBungeeCord().isEnabled()) {
      ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(outputByteArray);
      try {
        output.writeUTF(nickname);
        output.writeUTF(command);

        target.sendPluginMessage(
            this, this.config.getChannels().getBungeeCord().getName(), outputByteArray.toByteArray()
        );
      } catch (IOException ex) {
        target.sendMessage(ChatColor.RED + "Fatal error: " + ex.getMessage());
        target.sendMessage(ChatColor.RED + "More information is available in the console.");

        ex.printStackTrace();
      } finally {
        try {
          output.close();
          outputByteArray.close();
        } catch (IOException ignored) {
        }
      }
    }
  }

  @Override
  public boolean isBukkit() {
    return true;
  }

  @Override
  public void onPluginMessageReceived(String string, Player player, byte[] bytes) {
    DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
    try {
      String nickname = input.readUTF();
      String command = input.readUTF();

      Player target = this.getServer().getPlayer(nickname);
      if (target == null) {
        return;
      }

      target.performCommand(command);
    } catch (IOException ex) {
      player.sendMessage(ChatColor.RED + "Fatal error: " + ex.getMessage());
      player.sendMessage(ChatColor.RED + "More information is available in the console.");

      ex.printStackTrace();
    } finally {
      try {
        input.close();
      } catch (IOException ignored) {
      }
    }
  }

}
