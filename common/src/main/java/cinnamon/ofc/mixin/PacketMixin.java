package cinnamon.ofc.forge.mixin;

import cinnamon.ofc.Getter;
import cinnamon.ofc.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundInteractPacket.class)
public class PacketMixin implements Getter {

    @Unique
    public boolean doOverride;

    @Inject(method = "<init>(IZLnet/minecraft/network/protocol/game/ServerboundInteractPacket$Action;)V", at = @At(value = "RETURN"))
    public void init(int i, boolean bl, ServerboundInteractPacket.Action action, CallbackInfo ci) {
        if(Minecraft.getInstance().player != null) {
            this.doOverride = Mod.get(Minecraft.getInstance().player).doOverride;
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "RETURN"))
    public void init(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        this.doOverride = friendlyByteBuf.readBoolean();
    }

    @Inject(method = "write", at = @At(value = "RETURN"))
    public void write(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        friendlyByteBuf.writeBoolean(this.doOverride);
    }

    @Override
    public boolean get() {
        return this.doOverride;
    }
}
