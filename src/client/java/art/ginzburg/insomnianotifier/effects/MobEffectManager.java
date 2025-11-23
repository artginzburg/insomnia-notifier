package art.ginzburg.insomnianotifier.effects;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class MobEffectManager {
  private final LocalPlayer player;
  private final Holder<MobEffect> effect;

  private EffectKey currentEffect = null;

  public MobEffectManager(LocalPlayer player, Holder<MobEffect> effect) {
    this.player = player;
    this.effect = effect;
  }

  /**
   * Updates the effect on the player based on a condition.
   * Only one variant of the effect can be active at a time.
   */
  public void updateEffect(int amplifier, boolean ambient, boolean shouldHave) {
    EffectKey key = new EffectKey(amplifier, ambient);

    if (shouldHave && !key.equals(currentEffect)) {
      player.removeEffect(effect);
      player.addEffect(new MobEffectInstance(effect, -1, amplifier, ambient, true));
      // System.out.println("Added effect: " + effect + " amplifier=" + amplifier + "
      // ambient=" + ambient);
      currentEffect = key;
    } else if (!shouldHave && key.equals(currentEffect)) {
      player.removeEffect(effect);
      // System.out.println("Removed effect: " + effect);
      currentEffect = null;
    }
  }

  private record EffectKey(int amplifier, boolean ambient) {
  }
}
