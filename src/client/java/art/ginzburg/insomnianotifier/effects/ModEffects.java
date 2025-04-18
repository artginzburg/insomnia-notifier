package art.ginzburg.insomnianotifier.effects;

import art.ginzburg.insomnianotifier.InsomniaNotifier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

public class ModEffects {
  private static final MobEffect INSOMNIA_EFFECT = new InsomniaEffect();
  public static Holder<MobEffect> INSOMNIA = null;

  public static void registerEffects() {
    INSOMNIA = register("insomnia", INSOMNIA_EFFECT);
  }

  private static Holder<MobEffect> register(String id, MobEffect statusEffect) {
    return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
        ResourceLocation.fromNamespaceAndPath(InsomniaNotifier.MOD_ID, id),
        statusEffect);
  }
}
