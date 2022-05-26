package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "HEAD"))
    public void attackTargetEntityWithCurrentItem(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if( Capabilities.offHand(player).map(c -> c.ticksSinceLastSwing).orElse(0) <= 5 || player.ticksSinceLastSwing <= 5) {
            if (target != null) {
                target.hurtResistantTime = 0;
                if(target instanceof LivingEntity) {
                    ((LivingEntity) target).lastDamage = 0;
                }
            }
        }
    }
}
