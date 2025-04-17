package art.ginzburg.insomnianotifier.state;

import java.util.HashMap;
import java.util.Map;

public class SleepTrackerManager {
  private static final Map<String, FileBackedSleepTracker> TRACKERS = new HashMap<>();

  public static void loadTracker(String worldId) {
    FileBackedSleepTracker tracker = FileBackedSleepTracker.load(worldId);
    TRACKERS.put(worldId, tracker);
    System.out.println("Loaded tracker!!");
  }

  public static FileBackedSleepTracker get(String worldId) {
    return TRACKERS.get(worldId);
  }

  public static void saveTracker(String worldId) {
    FileBackedSleepTracker tracker = get(worldId);
    if (tracker != null) {
      tracker.save(worldId);
      System.out.println("Saved tracker!!");
    } else
      System.err.println("Could not save the tracker, because it was null.");
  }

  public static void clear() {
    TRACKERS.clear();
  }
}
