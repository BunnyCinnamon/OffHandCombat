package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.api.capability.OffHandCapability;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderer.class)
public abstract class HeldItemMixin {

    @ModifyVariable(method = "renderItemInFirstPerson(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER), name = "f5")
    public float setMainHandSwing(float f5, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, ClientPlayerEntity playerEntityIn) {
        OffHandCapability c = Capabilities.offHand(playerEntityIn).filter(cc -> cc.isActive).orElse(null);
        Hand hand = MoreObjects.firstNonNull(playerEntityIn.swingingHand, Hand.MAIN_HAND);
        return (c != null && c.isSwingInProgress && c.swingingHand == Hand.MAIN_HAND) ? getSwingProgress(c, partialTicks) : hand == Hand.MAIN_HAND ? playerEntityIn.getSwingProgress(partialTicks) : 0F;
    }

    @ModifyVariable(method = "renderItemInFirstPerson(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER), name = "f6")
    public float setOffHandSwing(float f6, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, ClientPlayerEntity playerEntityIn) {
        OffHandCapability c = Capabilities.offHand(playerEntityIn).filter(cc -> cc.isActive).orElse(null);
        Hand hand = MoreObjects.firstNonNull(playerEntityIn.swingingHand, Hand.MAIN_HAND);
        return (c != null && c.isSwingInProgress && c.swingingHand == Hand.OFF_HAND) ? getSwingProgress(c, partialTicks) : hand == Hand.OFF_HAND ? playerEntityIn.getSwingProgress(partialTicks) : 0F;
    }

    public float getSwingProgress(OffHandCapability c, float partialTickTime) {
        float f = c.swingProgress - c.prevSwingProgress;
        if (f < 0.0F) {
            ++f;
        }

        return c.prevSwingProgress + f * partialTickTime;
    }
}
