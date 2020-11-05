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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FirstPersonRenderer.class)
public abstract class HeldItemMixin {

    @Shadow
    private float equippedProgressOffHand;
    public boolean requipO;

    @Inject(method = "tick()V", at = @At(target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z", value = "INVOKE_ASSIGN", shift = At.Shift.BEFORE, ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void getReEquip(CallbackInfo ci, ClientPlayerEntity clientplayerentity, ItemStack itemstack, ItemStack itemstack1, float f, boolean requipM, boolean requipO) {
        this.requipO = requipO;
    }

    @Redirect(method = "tick()V", at = @At(target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", value = "INVOKE", ordinal = 3))
    public float tickHand(float num, float min, float max) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return 1F; //Miss!

        ItemStack offhand = player.getHeldItemOffhand();
        ItemStack mainHand = player.getHeldItemMainhand();
        int ticksSinceLastSwingOff = Capabilities.offHand(player).map(c -> c.ticksSinceLastSwing).orElse(0);
        int ticksSinceLastSwingMain = player.ticksSinceLastSwing;

        OffHandHandler.makeActive(player, offhand, mainHand);
        player.ticksSinceLastSwing = ticksSinceLastSwingOff;
        float f0 = player.getCooledAttackStrength(1F);
        player.ticksSinceLastSwing = ticksSinceLastSwingMain;
        OffHandHandler.makeInactive(player, offhand, mainHand);

        return MathHelper.clamp((!requipO ? f0 * f0 * f0 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyVariable(method = "renderItemInFirstPerson(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER), name = "f5")
    public float setMainHandSwing(float f5, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, ClientPlayerEntity playerEntityIn) {
        OffHandCapability c = Capabilities.offHand(playerEntityIn).orElse(null);
        Hand hand = MoreObjects.firstNonNull(playerEntityIn.swingingHand, Hand.MAIN_HAND);
        return (c != null && c.isSwingInProgress && c.swingingHand == Hand.MAIN_HAND) ? getSwingProgress(c, partialTicks) : hand == Hand.MAIN_HAND ? playerEntityIn.getSwingProgress(partialTicks) : 0F;
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyVariable(method = "renderItemInFirstPerson(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER), name = "f6")
    public float setOffHandSwing(float f6, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, ClientPlayerEntity playerEntityIn) {
        OffHandCapability c = Capabilities.offHand(playerEntityIn).orElse(null);
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
