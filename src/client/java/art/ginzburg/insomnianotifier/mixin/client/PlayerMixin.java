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

@Mixin(LocalPlayer.class)
public class PlayerMixin {
  private static int ticksUntilPhantoms = 72000;

  private boolean wasDead = false;

  private final MobEffectManager effectManager = new MobEffectManager(getThis(), ModEffects.INSOMNIA);

  private String worldId = null;
  private FileBackedSleepTracker tracker = null;

  private static Minecraft client = null;

  @Inject(method = "tick", at = @At("TAIL"))
  private void onTick(CallbackInfo ci) {
    LocalPlayer player = getThis();
    if (!player.clientLevel.isClientSide)
      return;

    if (client == null) {
      client = Minecraft.getInstance();
      return;
    }

    tracker = initializeTracker();
    if (tracker == null)
      return;

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

    effectManager.updateEffect(0, false, shouldHaveInsomniaEffect && !phantomsCanSpawn);
    effectManager.updateEffect(1, true, phantomsCanSpawn);
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

  private FileBackedSleepTracker initializeTracker() {
    if (worldId == null) {
      worldId = WorldIdUtil.getWorldId(client);
    }

    if (tracker == null) {
      tracker = SleepTrackerManager.get(worldId);
    }

    return tracker;
  }

  private LocalPlayer getThis() {
    return (LocalPlayer) (Object) this;
  }
}
