package net.mcreator.odito.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Регистрация типа генератора Камуи. Работает независимо от сгенерированного
 * кода MCreator: Forge сам вызывает класс через @Mod.EventBusSubscriber.
 */
@Mod.EventBusSubscriber(modid = "odito", bus = Mod.EventBusSubscriber.Bus.MOD)
public class KamuiRegistry {

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.CHUNK_GENERATOR, helper ->
                helper.register(new ResourceLocation("odito", "kamui"),
                        KamuiChunkGenerator.CODEC));
    }
}
