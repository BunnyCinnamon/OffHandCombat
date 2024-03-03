package cinnamon.ofc.mixin;

import cinnamon.ofc.Packet;
import cinnamon.ofc.Mod;
import cinnamon.ofc.Listener;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/network/ServerGamePacketListenerImpl$1")
public class ListenerImplMixin implements Listener {

    @Unique
    public ServerPlayer player;
    @Unique
    public ServerboundInteractPacket serverboundInteractPacket;

    @Dynamic
    @Inject(method = "onAttack()V", at = @At(target = "Lnet/minecraft/server/level/ServerPlayer;attack(Lnet/minecraft/world/entity/Entity;)V", value = "INVOKE", shift = At.Shift.BEFORE))
    public void onAttack(CallbackInfo ci) {
        if (((Packet) this.serverboundInteractPacket).get()) {
            Mod.Data data = Mod.get(this.player);
            if(data.attackStrengthTicker <= 5 || player.attackStrengthTicker <= 5) {
                Entity target = this.serverboundInteractPacket.getTarget(this.player.serverLevel());
                if (target != null) {
                    target.invulnerableTime = 0;
                    if(target instanceof LivingEntity) {
                        ((LivingEntity) target).lastHurt = 0;
                    }
                }
            }
            data.doOverride = true;
            this.player = null;
            this.serverboundInteractPacket = null;
        }
    }

    @Override
    public void setPlayer(ServerPlayer serverPlayer) {
        this.player = serverPlayer;
    }

    @Override
    public void setPacket(ServerboundInteractPacket serverboundInteractPacket) {
        this.serverboundInteractPacket = serverboundInteractPacket;
    }
}