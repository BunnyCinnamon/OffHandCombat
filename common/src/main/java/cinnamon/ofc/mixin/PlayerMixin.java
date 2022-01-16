package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {

    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "resetAttackStrengthTicker()V", at = @At(value = "HEAD"))
    public void resetAttackStrengthTicker(CallbackInfo ci) {
        Player player = getThis();
        if (HandPlatform.canUseOffhand(player) && HandPlatform.canSwingHand(player, InteractionHand.OFF_HAND)) {
            HandPlatform.resetAttackStrengthTickerMainHand(player);
        }
    }

    private Player getThis() {
        return (Player) (Object) this;
    }
}
