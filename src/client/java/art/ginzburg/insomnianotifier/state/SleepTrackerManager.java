package art.ginzburg.insomnianotifier.state;

import java.util.HashMap;
import java.util.Map;

import art.ginzburg.insomnianotifier.InsomniaNotifier;

public class SleepTrackerManager {
  private static final Map<String, FileBackedSleepTracker> TRACKERS = new HashMap<>();

  public static void loadTracker(String worldId) {
    FileBackedSleepTracker tracker = FileBackedSleepTracker.load(worldId);
    TRACKERS.put(worldId, tracker);
  }

  public static FileBackedSleepTracker get(String worldId) {
    return TRACKERS.get(worldId);
  }

  public static void saveTracker(String worldId) {
    FileBackedSleepTracker tracker = get(worldId);
    if (tracker != null) {
      tracker.save(worldId);
    } else {
      InsomniaNotifier.LOGGER.error("Couldn't save sleep tracker for " + worldId);
    }
  }

  public static void clear() {
    TRACKERS.clear();
  }
}
