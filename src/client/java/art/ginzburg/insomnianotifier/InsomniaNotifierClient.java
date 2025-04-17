package art.ginzburg.insomnianotifier;

import art.ginzburg.insomnianotifier.effects.ModEffects;
import art.ginzburg.insomnianotifier.state.SleepTrackerManager;
import art.ginzburg.insomnianotifier.util.WorldIdUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action;

public class InsomniaNotifierClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ModEffects.registerEffects();

    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
      String worldId = WorldIdUtil.getWorldId(client);
      SleepTrackerManager.loadTracker(worldId);

      client.getConnection().send(new ServerboundClientCommandPacket(Action.REQUEST_STATS));
    });

    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {

      String worldId = WorldIdUtil.getWorldId(client);
      SleepTrackerManager.saveTracker(worldId);

      SleepTrackerManager.clear();
    });
  }
}
