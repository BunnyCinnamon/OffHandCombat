package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.OHCConfig;
import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerHeldMixin {

    @Inject(method = "resetCooldown()V", at = @At(value = "HEAD"))
    public void resetCooldown(CallbackInfo ci) {
        //noinspection ConstantConditions
        PlayerEntity player = (PlayerEntity) ((Object) this);
        if (OffHandHandler.canSwingHand(player, Hand.OFF_HAND)) {
            int ticksSinceLastSwingOff = Capabilities.offHand(player).lazyMap(c -> c.ticksSinceLastSwing).orElse(0);
            ItemStack offhand = player.getHeldItemOffhand();
            ItemStack mainHand = player.getHeldItemMainhand();

            //Get half tick for offhand
            OffHandHandler.makeActive(player, offhand, mainHand);
            int halfTick = (int) (OHCConfig.Runtime.attackCooldownSetAfterSwing * player.getCooldownPeriod());
            OffHandHandler.makeInactive(player, offhand, mainHand);

            //Set half tick
            if (ticksSinceLastSwingOff > halfTick) {
                Capabilities.offHand(player).ifPresent(c -> c.ticksSinceLastSwing = halfTick);
            }
        }
    }
}
