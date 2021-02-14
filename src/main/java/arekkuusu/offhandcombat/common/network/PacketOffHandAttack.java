package arekkuusu.offhandcombat.common.network;

import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOffHandAttack {

    public int entityIdTarget;

    public PacketOffHandAttack(int entityIdTarget) {
        this.entityIdTarget = entityIdTarget;
    }

    public static void encode(PacketOffHandAttack msg, PacketBuffer buf) {
        buf.writeVarInt(msg.entityIdTarget);
    }

    public static PacketOffHandAttack decode(PacketBuffer buf) {
        return new PacketOffHandAttack(buf.readVarInt());
    }

    public static class Handler {
        public static void handle(final PacketOffHandAttack message, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().enqueueWork(() -> OffHandHandler.attackEntity(ctx.get().getSender(), ctx.get().getSender().getEntityWorld().getEntityByID(message.entityIdTarget)));
            }
            ctx.get().setPacketHandled(true);
        }
    }
}
