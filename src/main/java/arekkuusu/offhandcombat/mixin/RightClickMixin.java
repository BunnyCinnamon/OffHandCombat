package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.OHCConfig;
import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.api.capability.OffHandCapability;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import arekkuusu.offhandcombat.common.network.OHCPacketHandler;
import arekkuusu.offhandcombat.common.network.PacketOffHandAttack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class RightClickMixin {

    @Shadow
    public ClientPlayerEntity player;

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "processKeyBinds()V", at = @At(target = "Lnet/minecraft/client/Minecraft;rightClickMouse()V", value = "INVOKE", ordinal = 0))
    public void processKeyBinds(Minecraft minecraft) {
        if (!player.isRowingBoat() && !player.isSneaking() && OffHandHandler.canUseOffhand(player)) {
            net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(1, minecraft.gameSettings.keyBindAttack, Hand.OFF_HAND);
            if (!inputEvent.isCanceled() && minecraft.objectMouseOver.getType() != RayTraceResult.Type.BLOCK && OffHandHandler.canSwingHand(player, Hand.OFF_HAND)) {
                if (minecraft.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                    if (minecraft.playerController.getCurrentGameType() != GameType.SPECTATOR) {
                        OffHandHandler.attackEntity(player, ((EntityRayTraceResult) minecraft.objectMouseOver).getEntity());
                        OHCPacketHandler.INSTANCE.sendToServer(new PacketOffHandAttack(((EntityRayTraceResult) minecraft.objectMouseOver).getEntity().getEntityId()));
                    }
                } else {
                    Capabilities.offHand(player).ifPresent(c -> c.ticksSinceLastSwing = 0);
                    if (OffHandHandler.canSwingHand(player, Hand.MAIN_HAND)) {
                        int halfTick = (int) (OHCConfig.Runtime.attackCooldownSetAfterSwing * player.getCooldownPeriod());
                        if (player.ticksSinceLastSwing > halfTick) {
                            player.ticksSinceLastSwing = halfTick;
                        }
                    }
                }
                if (inputEvent.shouldSwingHand()) {
                    player.swingArm(Hand.OFF_HAND);
                    if (player.getHeldItem(Hand.OFF_HAND).getItem().getUseAction(player.getHeldItem(Hand.OFF_HAND)) == UseAction.NONE) {
                        minecraft.gameSettings.keyBindUseItem.unpressKey();
                    }
                }
                return;
            }
        }
        //Fallback
        rightClickMouse();
    }

    @ModifyVariable(method = "rightClickMouse()V", at = @At(target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 2), name = "itemstack")
    public ItemStack rightClickMouse(ItemStack itemStack) {
        return Capabilities.offHand(this.player).filter(c -> c.isActive).map(c -> {
            Hand hand = this.player.getHeldItem(Hand.MAIN_HAND) == itemStack ? Hand.MAIN_HAND : Hand.OFF_HAND;
            if (c.ticksSinceLastActiveStack < 3 && c.handOfLastActiveStack == hand) {
                return ItemStack.EMPTY;
            } else return itemStack;
        }).orElse(itemStack);
    }

    @Shadow
    protected abstract void rightClickMouse();
}
