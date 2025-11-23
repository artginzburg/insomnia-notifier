package art.ginzburg.insomnianotifier.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import art.ginzburg.insomnianotifier.effects.MobEffectManager;
import art.ginzburg.insomnianotifier.effects.ModEffects;
import art.ginzburg.insomnianotifier.state.FileBackedSleepTracker;
import art.ginzburg.insomnianotifier.state.SleepTrackerManager;
import art.ginzburg.insomnianotifier.util.WorldIdUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin {
  private static int ticksUntilPhantoms = 72000;

  private boolean wasDead = false;

  private final MobEffectManager effectManager = new MobEffectManager();

  private static Minecraft client = null;

  @Inject(method = "tick", at = @At("TAIL"))
  private void onTick(CallbackInfo ci) {
    if (!(((Object) this instanceof LocalPlayer player)))
      return;
    if (!player.clientLevel.isClientSide)
      return;

    if (client == null) {
      client = Minecraft.getInstance();
      return;
    }

    String worldId = WorldIdUtil.getWorldId(client);
    FileBackedSleepTracker tracker = SleepTrackerManager.get(worldId);
    if (tracker == null) {
      return;
    }

    boolean isDeadOrDying = player.isDeadOrDying();
    if (isDeadOrDying && !wasDead) {
      wasDead = true;
      tracker.resetTimeSinceRest();
      return;
    } else if (!isDeadOrDying) {
      wasDead = false;
    }

    if (player.isSleeping()) {
      tracker.resetTimeSinceRest();
    } else {
      tracker.updateTimeSinceRest();
    }

    int serverProvidedTimeSinceRest = player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
    if (serverProvidedTimeSinceRest != 0 && tracker.lastServerKnownTimeSinceRest != serverProvidedTimeSinceRest) {
      tracker.lastServerKnownTimeSinceRest = serverProvidedTimeSinceRest;
      tracker.setTimeSinceRest(serverProvidedTimeSinceRest);
    }

    boolean shouldHaveInsomniaEffect = tracker.getTimeSinceRest() >= ticksUntilPhantoms
        && player.clientLevel.dimensionType().bedWorks();

    boolean phantomsCanSpawn = shouldHaveInsomniaEffect && canSpawnPhantoms(player);

    effectManager.updateEffect(player, ModEffects.INSOMNIA, 0, shouldHaveInsomniaEffect && !phantomsCanSpawn);
    effectManager.updateEffect(player, ModEffects.PHANTOM_SPAWNING, 1, phantomsCanSpawn);
  }

  private boolean canSpawnPhantoms(LocalPlayer player) {
    BlockPos playerPos = player.blockPosition();
    ClientLevel clientLevel = player.clientLevel;

    if (playerPos.getY() < clientLevel.getSeaLevel())
      return false;

    if (!clientLevel.canSeeSky(playerPos))
      return false;

    return clientLevel.isNight() || clientLevel.isThundering();
  }

}
