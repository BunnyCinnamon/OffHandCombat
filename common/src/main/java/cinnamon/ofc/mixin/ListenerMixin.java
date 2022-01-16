package cinnamon.ofc.mixin;

import cinnamon.ofc.Setter;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ListenerMixin {

    @Shadow
    public ServerPlayer player;
    @Unique
    public ServerboundInteractPacket serverboundInteractPacket;

    @Inject(method = "handleInteract", at = @At(target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;dispatch(Lnet/minecraft/network/protocol/game/ServerboundInteractPacket$Handler;)V", value = "INVOKE", shift = At.Shift.BEFORE))
    public void handleInteract(ServerboundInteractPacket serverboundInteractPacket, CallbackInfo ci) {
        this.serverboundInteractPacket = serverboundInteractPacket;
    }

    @ModifyArg(method = "handleInteract", at = @At(target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;dispatch(Lnet/minecraft/network/protocol/game/ServerboundInteractPacket$Handler;)V", value = "INVOKE"))
    public ServerboundInteractPacket.Handler handleInteract(ServerboundInteractPacket.Handler arg) {
        ((Setter) arg).setPlayer(this.player);
        ((Setter) arg).setPacket(this.serverboundInteractPacket);
        this.serverboundInteractPacket = null;
        return arg;
    }
}