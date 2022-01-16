package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import cinnamon.ofc.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(Minecraft.class)
public abstract class RightClickMixin {

    @Shadow
    public LocalPlayer player;
    @Shadow
    public HitResult hitResult;
    @Shadow
    public MultiPlayerGameMode gameMode;

    @Redirect(method = "handleKeybinds()V", at = @At(target = "Lnet/minecraft/client/Minecraft;startUseItem()V", value = "INVOKE", ordinal = 0))
    public void processKeyBinds(Minecraft minecraft) {
        if (!this.player.isHandsBusy() && !this.player.isCrouching() && HandPlatform.canUseOffhand(player) && HandPlatform.canSwingHand(this.player, InteractionHand.OFF_HAND)) {
            Mod.Data data = Mod.get(this.player);
            if (data.missTime <= 0 && this.hitResult != null) {
                data.doOverride = true;
                switch (this.hitResult.getType()) {
                    case ENTITY:
                        this.gameMode.attack(this.player, ((EntityHitResult) this.hitResult).getEntity());
                        this.player.swing(InteractionHand.OFF_HAND);
                        break;
                    case BLOCK:
                        break;
                    case MISS:
                        if (Objects.requireNonNull(this.gameMode).hasMissTime()) {
                            data.missTime = 10;
                        }

                        HandPlatform.resetAttackStrengthTickerOffHand(this.player);
                        this.player.swing(InteractionHand.OFF_HAND);
                        break;
                }
                data.doOverride = false;
            }
        }
        //Fallback
        startUseItem();
    }

    @ModifyVariable(method = "startUseItem()V", at = @At(target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 1), name = "itemStack")
    public ItemStack startUseItem(ItemStack itemStack) {
        Mod.Data data = Mod.get(this.player);
        if (data.swinging) {
            InteractionHand hand = this.player.getItemInHand(InteractionHand.MAIN_HAND) == itemStack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (data.ticksSinceLastActiveStack < 3 && data.handOfLastActiveStack == hand) {
                return ItemStack.EMPTY;
            } else return itemStack;
        } else {
            return itemStack;
        }
    }

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

    @Shadow
    protected abstract void startUseItem();
}
