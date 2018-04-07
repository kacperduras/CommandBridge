package pl.kacperduras.commandbridge.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.lang3.Validate;
import pl.kacperduras.commandbridge.shaded.CommandBridgeAPI;
import pl.kacperduras.commandbridge.shaded.CommandBridgeConfig;
import pl.kacperduras.commandbridge.shaded.util.ConfigUtils;

public final class CommandBridgeBungee extends Plugin implements CommandBridgeAPI, Listener {

  private CommandBridgeConfig config;

  @Override
  public void onLoad() {
    this.config = ConfigUtils.loadConfig(
        new File(this.getDataFolder(), "config.yml"), CommandBridgeConfig.class);
  }

  @Override
  public void onEnable() {
    if (this.config.getChannels().getBukkit().isEnabled()) {
      this.getProxy().registerChannel(this.config.getChannels().getBukkit().getName());
    }

    if (this.config.getChannels().getBungeeCord().isEnabled()) {
      this.getProxy().registerChannel(this.config.getChannels().getBungeeCord().getName());
    }

    this.getProxy().getPluginManager().registerListener(this, this);
  }

  @Override
  public void onDisable() {
    if (this.config.getChannels().getBukkit().isEnabled()) {
      this.getProxy().unregisterChannel(this.config.getChannels().getBukkit().getName());
    }

    if (this.config.getChannels().getBungeeCord().isEnabled()) {
      this.getProxy().unregisterChannel(this.config.getChannels().getBungeeCord().getName());
    }
  }

  @Override
  public void bukkit(String nickname, String server, String command) {
    Validate.notNull(nickname);
    Validate.notNull(server);
    Validate.notNull(command);

    ProxiedPlayer player = this.getProxy().getPlayer(nickname);
    if (player == null) {
      return;
    }

    ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
    DataOutputStream output = new DataOutputStream(outputByteArray);
    try {
      output.writeUTF(nickname);
      output.writeUTF(command);
    } catch (IOException ex) {
      player.sendMessage(ChatColor.RED + "Fatal error: " + ex.getMessage());
      player.sendMessage(ChatColor.RED + "More informations is available in the console.");

      ex.printStackTrace();
    } finally {
      try {
        output.close();
        outputByteArray.close();
      } catch (IOException ignored) {
      }
    }

    ServerInfo target = this.getProxy().getServerInfo(server);
    if (target == null) {
      return;
    }

    target.sendData(this.config.getChannels().getBukkit().getName(), outputByteArray.toByteArray());
  }

  @Override
  public void bungee(String nickname, String command) {
    throw new RuntimeException("This platform doesn't support sending messages to BungeeCord!");
  }

  @Override
  public boolean isBungee() {
    return true;
  }

  @EventHandler
  public void onReceive(PluginMessageEvent event) {
    String channel = event.getTag();
    if (!channel.equals(this.config.getChannels().getBungeeCord().getName())) {
      return;
    }

    DataInputStream input = new DataInputStream(new ByteArrayInputStream(event.getData()));
    try {
      String nickname = input.readUTF();
      String command = input.readUTF();

      ProxiedPlayer target = this.getProxy().getPlayer(nickname);
      if (target == null) {
        return;
      }

      this.getProxy().getPluginManager().dispatchCommand(target, command);
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
        input.close();
      } catch (IOException ignored) {
      }
    }
  }

}
