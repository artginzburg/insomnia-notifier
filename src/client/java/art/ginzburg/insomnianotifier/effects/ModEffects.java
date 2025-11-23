package art.ginzburg.insomnianotifier.effects;

import art.ginzburg.insomnianotifier.InsomniaNotifier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

public class ModEffects {
  private static final MobEffect INSOMNIA_EFFECT = new InsomniaEffect();
  private static final MobEffect PHANTOM_SPAWNING_EFFECT = new PhantomSpawningEffect();

  public static Holder<MobEffect> INSOMNIA = null;
  public static Holder<MobEffect> PHANTOM_SPAWNING = null;

  public static void registerEffects() {
    INSOMNIA = register("insomnia", INSOMNIA_EFFECT);
    PHANTOM_SPAWNING = register("phantom_spawning", PHANTOM_SPAWNING_EFFECT);
  }

  private static Holder<MobEffect> register(String id, MobEffect statusEffect) {
    return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
        ResourceLocation.fromNamespaceAndPath(InsomniaNotifier.MOD_ID, id),
        statusEffect);
  }
}
