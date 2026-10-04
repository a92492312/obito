package net.mcreator.odito.kamui;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = "odito", bus = Mod.EventBusSubscriber.Bus.MOD)
public class KamuiCommonSetup {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(KamuiNetworking::register);
    }
}
