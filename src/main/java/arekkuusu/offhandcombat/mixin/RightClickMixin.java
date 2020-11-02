package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import arekkuusu.offhandcombat.common.network.OHCPacketHandler;
import arekkuusu.offhandcombat.common.network.PacketOffHandAttack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class RightClickMixin {

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "processKeyBinds()V", at = @At(target = "Lnet/minecraft/client/Minecraft;rightClickMouse()V", value = "INVOKE"), expect = 2)
    public void rightClickMouse(Minecraft minecraft) {
        if (!minecraft.player.isRowingBoat()) {
            net.minecraftforge.client.event.InputEvent.ClickInputEvent inputEvent = net.minecraftforge.client.ForgeHooksClient.onClickInput(1, minecraft.gameSettings.keyBindAttack, Hand.OFF_HAND);
            if (!inputEvent.isCanceled() && minecraft.objectMouseOver.getType() != RayTraceResult.Type.BLOCK && OffHandHandler.canSwingHand(minecraft.player, Hand.OFF_HAND)) {
                if (minecraft.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                    if (minecraft.playerController.getCurrentGameType() != GameType.SPECTATOR) {
                        OffHandHandler.attackEntity(minecraft.player, ((EntityRayTraceResult) minecraft.objectMouseOver).getEntity());
                        OHCPacketHandler.INSTANCE.sendToServer(new PacketOffHandAttack(((EntityRayTraceResult) minecraft.objectMouseOver).getEntity().getEntityId()));
                    }
                } else {
                    Capabilities.offHand(minecraft.player).ifPresent(c -> c.ticksSinceLastSwing = 0);
                    if (OffHandHandler.canSwingHand(minecraft.player, Hand.MAIN_HAND)) {
                        int halfTick = (int) (0.5F * minecraft.player.getCooldownPeriod());
                        if (minecraft.player.ticksSinceLastSwing > halfTick) {
                            minecraft.player.ticksSinceLastSwing = halfTick;
                        }
                    }
                }
                if (inputEvent.shouldSwingHand()) {
                    minecraft.player.swingArm(Hand.OFF_HAND);
                    minecraft.gameSettings.keyBindUseItem.unpressKey();
                }
                return;
            }
        }
        //Fallback
        rightClickMouse();
    }

    @Shadow
    protected abstract void rightClickMouse();
}
