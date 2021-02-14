package arekkuusu.offhandcombat.common.network;

import arekkuusu.offhandcombat.OHC;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class OHCPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OHC.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(id++, PacketOffHandAttack.class, PacketOffHandAttack::encode, PacketOffHandAttack::decode, PacketOffHandAttack.Handler::handle);
        INSTANCE.registerMessage(id++, PacketOffHandAttackToggle.class, PacketOffHandAttackToggle::encode, PacketOffHandAttackToggle::decode, PacketOffHandAttackToggle.Handler::handle);
    }
}
