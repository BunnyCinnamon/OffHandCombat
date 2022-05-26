package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import cinnamon.ofc.Mod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class GameModeAttackMixin {

    @Inject(method = "attack", at = @At(target = "Lnet/minecraft/world/entity/player/Player;attack(Lnet/minecraft/world/entity/Entity;)V", value = "INVOKE"), cancellable = true)
    public void attack(Player player, Entity entity, CallbackInfo ci) {
        Mod.Data data = Mod.get(player);
        if(data.attackStrengthTicker <= 5 || player.attackStrengthTicker <= 5) {
            entity.invulnerableTime = 0;
            if(entity instanceof LivingEntity) {
                ((LivingEntity) entity).lastHurt = 0;
            }
        }
        if(data.doOverride) {
            HandPlatform.attack(player, entity);
            ci.cancel();
        }
    }
}
