package net.mcreator.odito.kamui;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "odito", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KamuiClientSetup {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.AddLayers event) {
        // Проходим по всем скинам игрока (default и slim) — так слой точно добавится всем
        for (String skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new KamuiOverlayLayer(renderer, new KamuiClientSetup()));
            }
        }
    }

    public ResourceLocation textureFor(int style) {
        switch (style) {
            case 1:
                return new ResourceLocation("odito", "textures/entity/sharingan_obito_overlay.png");
            case 2:
                return new ResourceLocation("odito", "textures/entity/rinnegan_obito_overlay.png");
            case 3:
                return new ResourceLocation("odito", "textures/entity/kamui_obito_overlay.png");
            default:
                return null;
        }
    }
}
