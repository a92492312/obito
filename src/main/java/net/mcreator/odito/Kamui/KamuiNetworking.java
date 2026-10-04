package net.mcreator.odito.kamui;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class KamuiNetworking {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("odito", "kamui_styles"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        CHANNEL.messageBuilder(KamuiStylePacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(KamuiStylePacket::encode)
                .decoder(KamuiStylePacket::decode)
                .consumerMainThread(KamuiStylePacket::handle)
                .add();
    }

    /** Разослать стиль глаз игрока ему самому и всем, кто его видит. */
    public static void sync(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new KamuiStylePacket(player.getUUID(), KamuiData.getEyeStyle(player)));
    }

    public static class KamuiStylePacket {
        public final UUID uuid;
        public final int style;

        public KamuiStylePacket(UUID uuid, int style) {
            this.uuid = uuid;
            this.style = style;
        }

        public static void encode(KamuiStylePacket msg, FriendlyByteBuf buf) {
            buf.writeUUID(msg.uuid);
            buf.writeVarInt(msg.style);
        }

        public static KamuiStylePacket decode(FriendlyByteBuf buf) {
            return new KamuiStylePacket(buf.readUUID(), buf.readVarInt());
        }

        public static void handle(KamuiStylePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() ->
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                            KamuiClientPacketHandler.handle(msg.uuid, msg.style)));
        }
    }
}
