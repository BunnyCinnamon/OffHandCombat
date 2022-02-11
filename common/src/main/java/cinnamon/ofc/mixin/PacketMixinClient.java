package cinnamon.ofc.mixin;

import cinnamon.ofc.Mod;
import cinnamon.ofc.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ServerboundInteractPacket.class)
public class PacketMixinClient {

    @Inject(method = "<init>(IZLnet/minecraft/network/protocol/game/ServerboundInteractPacket$Action;)V", at = @At(value = "RETURN"))
    public void init(int i, boolean bl, ServerboundInteractPacket.Action action, CallbackInfo ci) {
        if(Minecraft.getInstance().player != null) {
            ((Packet)this).set(Mod.get(Minecraft.getInstance().player).doOverride);
        }
    }
}
