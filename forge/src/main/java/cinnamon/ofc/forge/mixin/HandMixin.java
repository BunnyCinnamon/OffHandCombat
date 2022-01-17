package cinnamon.ofc.forge.mixin;

import cinnamon.ofc.HandPlatform;
import cinnamon.ofc.Mod;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ItemInHandRenderer.class)
public abstract class HandMixin {

    @Shadow
    private float offHandHeight;
    @Shadow
    private ItemStack offHandItem;

    @Redirect(method = "tick", at = @At(target = "net/minecraft/util/Mth.clamp (FFF)F", value = "INVOKE", ordinal = 3))
    public float tickHand(float num, float min, float max) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return Mth.clamp(num, min, max);
        if (!HandPlatform.canUseOffhand(player)) return Mth.clamp(num, min, max);

        ItemStack offhand = player.getOffhandItem();
        ItemStack mainHand = player.getMainHandItem();
        int ticksSinceLastSwingOff = Mod.get(player).attackStrengthTicker;
        int ticksSinceLastSwingMain = player.attackStrengthTicker;

        HandPlatform.makeActive(player, offhand, mainHand);
        player.attackStrengthTicker = ticksSinceLastSwingOff;
        float f0 = Mth.clamp(((float) Mod.get(player).attackStrengthTicker + Minecraft.getInstance().getDeltaFrameTime()) / player.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
        player.attackStrengthTicker = ticksSinceLastSwingMain;
        HandPlatform.makeInactive(player, offhand, mainHand);

        boolean reequip = ForgeHooksClient.shouldCauseReequipAnimation(this.offHandItem, offhand, -1);
        return Mth.clamp((!reequip ? (f0 * f0 * f0) : 0F) - this.offHandHeight, -0.4F, 0.4F);
    }

    @Redirect(method = "renderHandsWithItems", at = @At(target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", value = "INVOKE", ordinal = 0))
    public void renderArmWithItemMain(ItemInHandRenderer instance, AbstractClientPlayer f3, float f4, float f9, InteractionHand f13, float f, ItemStack f1, float f2, PoseStack flag1, MultiBufferSource flag2, int i) {
        this.renderArmWithItem(f3, f4, f9, f13, getMainHandSwing(Minecraft.getInstance().getDeltaFrameTime()), f1, f2, flag1, flag2, i);
    }

    @Redirect(method = "renderHandsWithItems", at = @At(target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", value = "INVOKE", ordinal = 1))
    public void renderArmWithItemOff(ItemInHandRenderer instance, AbstractClientPlayer f3, float f4, float f9, InteractionHand f13, float f, ItemStack f1, float f2, PoseStack flag1, MultiBufferSource flag2, int i) {
        this.renderArmWithItem(f3, f4, f9, f13, getOffHandSwing(Minecraft.getInstance().getDeltaFrameTime()), f1, f2, flag1, flag2, i);
    }

    public float getMainHandSwing(float partial) {
        Mod.Data data = Mod.get(Objects.requireNonNull(Minecraft.getInstance().player));
        InteractionHand hand = MoreObjects.firstNonNull(Minecraft.getInstance().player.swingingArm, InteractionHand.MAIN_HAND);
        return (data.swinging && data.swingingArm == InteractionHand.MAIN_HAND)
                ? getSwingProgress(data, partial) : hand == InteractionHand.MAIN_HAND
                ? Minecraft.getInstance().player.getAttackAnim(partial) : 0F;
    }

    public float getOffHandSwing(float partial) {
        Mod.Data data = Mod.get(Objects.requireNonNull(Minecraft.getInstance().player));
        InteractionHand hand = MoreObjects.firstNonNull(Minecraft.getInstance().player.swingingArm, InteractionHand.MAIN_HAND);
        return (data.swinging && data.swingingArm == InteractionHand.OFF_HAND)
                ? getSwingProgress(data, partial) : hand == InteractionHand.OFF_HAND
                ? Minecraft.getInstance().player.getAttackAnim(partial) : 0F;
    }

    public float getSwingProgress(Mod.Data data, float partialTickTime) {
        float f = data.attackAnim - data.attackAnim_;
        if (f < 0.0F) {
            ++f;
        }

        return data.attackAnim_ + f * partialTickTime;
    }

    @Shadow
    protected abstract void renderArmWithItem(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j);

}
