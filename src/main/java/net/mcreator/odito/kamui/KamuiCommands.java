package net.mcreator.odito.kamui;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "odito", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KamuiCommands {

    /** Имя стиля -> число. */
    private static int parseStyle(String name) {
        return switch (name.toLowerCase()) {
            case "kamui" -> KamuiData.EYES_KAMUI;
            case "sharingan" -> KamuiData.EYES_SHARINGAN;
            case "rinnegan" -> KamuiData.EYES_RINNEGAN;
            default -> KamuiData.EYES_NONE;
        };
    }

    private static String styleName(int style) {
        return switch (style) {
            case KamuiData.EYES_KAMUI -> "Камуи";
            case KamuiData.EYES_SHARINGAN -> "Шаринган";
            case KamuiData.EYES_RINNEGAN -> "Риннеган";
            default -> "обычные";
        };
    }

    @SubscribeEvent
    public static void onRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("kamui")
                .then(Commands.literal("eyes")
                        .then(Commands.literal("none")
                                .executes(ctx -> set(ctx.getSource().getPlayerOrException(), KamuiData.EYES_NONE)))
                        .then(Commands.literal("kamui")
                                .executes(ctx -> set(ctx.getSource().getPlayerOrException(), KamuiData.EYES_KAMUI)))
                        .then(Commands.literal("sharingan")
                                .executes(ctx -> set(ctx.getSource().getPlayerOrException(), KamuiData.EYES_SHARINGAN)))
                        .then(Commands.literal("rinnegan")
                                .executes(ctx -> set(ctx.getSource().getPlayerOrException(), KamuiData.EYES_RINNEGAN)))));
    }

    private static int set(Player player, int style) {
        KamuiData.setEyeStyle(player, style);
        KamuiNetworking.sync(player);
        player.sendSystemMessage(Component.literal("Глаза: " + styleName(style)));
        return 1;
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        KamuiNetworking.sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        KamuiNetworking.sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        KamuiNetworking.sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player player) {
            KamuiNetworking.sync(player);
        }
    }
}
