package cinnamon.ofc.mixin;

import cinnamon.ofc.Getter;
import cinnamon.ofc.Mod;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net/minecraft/server/network/ServerGamePacketListenerImpl$1")
public class ListenerMixin {

    @Dynamic
    @Inject(method = "onAttack()V", at = @At(target = "Lnet/minecraft/server/level/ServerPlayer;attack(Lnet/minecraft/world/entity/Entity;)V", value = "INVOKE", shift = At.Shift.BEFORE), locals = LocalCapture.PRINT)
    public void onAttackBefore(ServerboundInteractPacket serverboundInteractPacket, CallbackInfo ci, ServerPlayer player) {
        if (((Getter) serverboundInteractPacket).get()) {
            Mod.get(player).doOverride = true;
        }
    }

    @Dynamic
    @Inject(method = "onAttack()V", at = @At(target = "Lnet/minecraft/server/level/ServerPlayer;attack(Lnet/minecraft/world/entity/Entity;)V", value = "INVOKE", shift = At.Shift.AFTER), locals = LocalCapture.PRINT)
    public void onAttackAfter(ServerboundInteractPacket serverboundInteractPacket, CallbackInfo ci, ServerPlayer player) {
        if (((Getter) serverboundInteractPacket).get()) {
            Mod.get(player).doOverride = false;
        }
    }
}