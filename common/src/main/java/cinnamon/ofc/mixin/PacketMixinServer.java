package cinnamon.ofc.mixin;

import cinnamon.ofc.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundInteractPacket.class)
public class PacketMixinServer implements Packet {

    @Unique
    public boolean doOverride;

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "RETURN"))
    public void init(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        ((Packet)this).set(friendlyByteBuf.readBoolean());
    }

    @Inject(method = "write", at = @At(value = "RETURN"))
    public void write(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        friendlyByteBuf.writeBoolean(((Packet)this).get());
    }

    @Override
    public boolean get() {
        return this.doOverride;
    }

    @Override
    public void set(boolean set) {
        this.doOverride = set;
    }
}
