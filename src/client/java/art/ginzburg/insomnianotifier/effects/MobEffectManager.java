package art.ginzburg.insomnianotifier.effects;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class MobEffectManager {
  private final Map<Holder<MobEffect>, Boolean> trackedEffects = new HashMap<>();

  /**
   * Updates a mob effect on the player based on a condition.
   * Adds the effect if the condition is true and it hasn't been added yet.
   * Removes the effect if the condition is false and it was previously added.
   */
  public void updateEffect(Player player, Holder<MobEffect> effect, int amplifier, boolean shouldHave) {
    boolean hasEffect = trackedEffects.getOrDefault(effect, false);

    if (shouldHave && !hasEffect) {
      MobEffectInstance effectInstance = new MobEffectInstance(effect, -1, amplifier);
      player.addEffect(effectInstance);
      trackedEffects.put(effect, true);
    } else if (!shouldHave && hasEffect) {
      player.removeEffect(effect);
      trackedEffects.put(effect, false);
    }
  }
}
