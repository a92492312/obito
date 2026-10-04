package net.mcreator.odito.kamui;

import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "odito", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KamuiClientSetup {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.AddLayers event) {
        if (event.getRenderer() instanceof PlayerRenderer playerRenderer) {
            playerRenderer.addLayer(new KamuiOverlayLayer(playerRenderer, new KamuiClientSetup()));
        }
    }

    public KamuiClientSetup() {
        // Пустой конструктор
    }

    public ResourceLocation textureFor(int style) {
        switch (style) {
            case 1:
                return new ResourceLocation("odito", "textures/entity/sharingan_obito_overlay.png");
            case 2:
                return new ResourceLocation("odito", "textures/entity/mangekyo_obito_overlay.png");
            default:
                return null;
        }
    }
}
