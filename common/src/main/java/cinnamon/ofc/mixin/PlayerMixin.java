package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {

    @Unique
    private ItemStack lastItemInOffHand = ItemStack.EMPTY;

    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", shift = At.Shift.BEFORE))
    public void resetReEquipAttackStrengthTicker(CallbackInfo ci) {
        Player player = getThis();
        if (HandPlatform.canUseOffhand(player)) {
            ItemStack itemstack = player.getOffhandItem();
            if (!ItemStack.matches(this.lastItemInOffHand, itemstack)) {
                if (!ItemStack.isSameIgnoreDurability(this.lastItemInOffHand, itemstack)
                        && HandPlatform.canSwingHand(player, InteractionHand.OFF_HAND)) {
                    HandPlatform.resetAttackStrengthTickerOffHand(player);
                }
                this.lastItemInOffHand = itemstack.copy();
            }
        }
    }

    private Player getThis() {
        return (Player) (Object) this;
    }
}
