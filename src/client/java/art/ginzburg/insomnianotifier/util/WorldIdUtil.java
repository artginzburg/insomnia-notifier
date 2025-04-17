package art.ginzburg.insomnianotifier.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.util.Locale;

public class WorldIdUtil {
  public static String getWorldId(Minecraft client) {

    ServerData server = client.getCurrentServer();
    if (server != null) {
      // Multiplayer — use server address
      return normalize("server_" + server.ip);
    }

    if (client.isSingleplayer() && client.getSingleplayerServer() != null) {
      // Singleplayer — use save folder name
      return normalize("local_" + client.getSingleplayerServer().getWorldData().getLevelName());
    }

    return "unknown_world";
  }

  private static String normalize(String raw) {
    return raw.toLowerCase(Locale.ROOT)
        .replaceAll("[^a-z0-9-_]", "_");
  }
}
