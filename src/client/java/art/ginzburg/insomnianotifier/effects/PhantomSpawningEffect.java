package art.ginzburg.insomnianotifier.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PhantomSpawningEffect extends MobEffect {
  public PhantomSpawningEffect() {
    super(MobEffectCategory.HARMFUL, 0xFF0000); // Red color
  }
}
