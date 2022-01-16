package cinnamon.ofc.forge.mixin;

import cinnamon.ofc.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class RightClickMixin {

    @Shadow
    public LocalPlayer player;

    @ModifyVariable(method = "startUseItem()V", at = @At(target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 1), name = "itemstack")
    public ItemStack startUseItem(ItemStack itemstack) {
        Mod.Data data = Mod.get(this.player);
        if (data.swinging) {
            InteractionHand hand = this.player.getItemInHand(InteractionHand.MAIN_HAND) == itemstack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (data.ticksSinceLastActiveStack < 3 && data.handOfLastActiveStack == hand) {
                return ItemStack.EMPTY;
            } else return itemstack;
        } else {
            return itemstack;
        }
    }
}
