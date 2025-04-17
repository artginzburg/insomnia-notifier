package art.ginzburg.insomnianotifier.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedSleepTracker {
  private static final Gson GSON = new GsonBuilder().create();
  public static final Path BASE_DIR = FabricLoader.getInstance()
      .getGameDir().resolve("cache/insomnia-tracker");

  public static FileBackedSleepTracker load(String worldId) {
    Path path = getFilePath(worldId);
    if (Files.exists(path)) {
      try {
        return GSON.fromJson(Files.readString(path), FileBackedSleepTracker.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return new FileBackedSleepTracker();
  }

  public void save(String worldId) {
    try {
      Files.createDirectories(BASE_DIR);
      Files.writeString(getFilePath(worldId), GSON.toJson(this));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Path getFilePath(String worldId) {
    return BASE_DIR.resolve(worldId + ".json");
  }

  private int timeSinceRest = -1;
  public int lastServerKnownTimeSinceRest = -1;

  public void setTimeSinceRest(int value) {
    this.timeSinceRest = value;
  }

  public void updateTimeSinceRest() {
    this.timeSinceRest++;
  }

  public void resetTimeSinceRest() {
    this.timeSinceRest = 0;
  }

  public int getTimeSinceRest() {
    return timeSinceRest;
  }

}
