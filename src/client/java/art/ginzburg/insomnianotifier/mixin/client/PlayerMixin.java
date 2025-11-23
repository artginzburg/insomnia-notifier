package art.ginzburg.insomnianotifier.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import art.ginzburg.insomnianotifier.effects.ModEffects;
import art.ginzburg.insomnianotifier.state.FileBackedSleepTracker;
import art.ginzburg.insomnianotifier.state.SleepTrackerManager;
import art.ginzburg.insomnianotifier.util.WorldIdUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin {
  private static int ticksUntilPhantoms = 72000;

  private boolean wasDead = false;

  private boolean hasAddedInsomniaEffect = false;
  private boolean hasRemovedInsomniaEffect = false;

  private boolean hasAddedPhantomSpawningEffect = false;
  private boolean hasRemovedPhantomSpawningEffect = false;

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

    if (shouldHaveInsomniaEffect && !phantomsCanSpawn) {
      if (!hasAddedInsomniaEffect) {
        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.INSOMNIA,
            -1, 0, true, true);
        player.addEffect(effectInstance);
        hasAddedInsomniaEffect = true;
        hasRemovedInsomniaEffect = false;
      }
    } else {
      if (!hasRemovedInsomniaEffect) {
        player.removeEffect(ModEffects.INSOMNIA);
        hasAddedInsomniaEffect = false;
        hasRemovedInsomniaEffect = true;
      }
    }

    if (phantomsCanSpawn) {
      if (!hasAddedPhantomSpawningEffect) {
        MobEffectInstance effectInstance = new MobEffectInstance(ModEffects.PHANTOM_SPAWNING,
            -1, 0, true, true);
        player.addEffect(effectInstance);
        hasAddedPhantomSpawningEffect = true;
        hasRemovedPhantomSpawningEffect = false;
      }
    } else {
      if (!hasRemovedPhantomSpawningEffect) {
        player.removeEffect(ModEffects.PHANTOM_SPAWNING);
        hasAddedPhantomSpawningEffect = false;
        hasRemovedPhantomSpawningEffect = true;
      }
    }
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
