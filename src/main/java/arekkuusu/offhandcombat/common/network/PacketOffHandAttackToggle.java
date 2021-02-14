package arekkuusu.offhandcombat.common.network;

import arekkuusu.offhandcombat.OHC;
import arekkuusu.offhandcombat.api.capability.Capabilities;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOffHandAttackToggle {

    public boolean isActive;

    public PacketOffHandAttackToggle(boolean isActive) {
        this.isActive = isActive;
    }

    public static void encode(PacketOffHandAttackToggle msg, PacketBuffer buf) {
        buf.writeBoolean(msg.isActive);
    }

    public static PacketOffHandAttackToggle decode(PacketBuffer buf) {
        return new PacketOffHandAttackToggle(buf.readBoolean());
    }

    public static class Handler {
        public static void handle(final PacketOffHandAttackToggle message, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                ctx.get().enqueueWork(() -> {
                    Capabilities.offHand(OHC.getProxy().getPlayer()).ifPresent(c -> c.isActive = message.isActive);
                });
            }
            ctx.get().setPacketHandled(true);
        }
    }
}
