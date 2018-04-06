package pl.kacperduras.commandbridge.shaded;

public interface CommandBridgeAPI {

  void bukkit(String nickname, String server, String command);

  void bungee(String nickname, String command);

  default boolean isBukkit() {
    return false;
  }

  default boolean isBungee() {
    return false;
  }

}
