package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(IngameGui.class)
public abstract class RenderCrossMixin extends AbstractGui {

    @Shadow
    protected int scaledHeight;
    @Shadow
    protected int scaledWidth;
    @Final
    @Shadow
    protected Minecraft mc;

    @Inject(method = "renderHotbar(F)V", at = @At(value = "FIELD", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/player/ClientPlayerEntity;", slice = "Lnet/minecraft/client/settings/AttackIndicatorStatus;HOTBAR:Lnet/minecraft/client/settings/AttackIndicatorStatus;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void renderHotBar(float p_194806_1_, CallbackInfo ci, PlayerEntity playerentity, ItemStack itemstack, HandSide handside, int i, int j, int k, int l) {
        PlayerEntity player = this.mc.player;
        if (player == null) return; //Miss!

        ItemStack offhand = player.getHeldItemOffhand();
        if(!OffHandHandler.canSwingHand(player, Hand.OFF_HAND)) return; //YOINK!
        ItemStack mainHand = player.getHeldItemMainhand();
        OffHandHandler.makeActive(player, offhand, mainHand);
        int ticksSinceLastSwingOff = Capabilities.offHand(player).map(c -> c.ticksSinceLastSwing).orElse(0);
        int ticksSinceLastSwingMain = player.ticksSinceLastSwing;
        player.ticksSinceLastSwing = ticksSinceLastSwingOff;
        float f = player.getCooledAttackStrength(0F);
        player.ticksSinceLastSwing = ticksSinceLastSwingMain;
        OffHandHandler.makeInactive(player, offhand, mainHand);

        if (f < 1F) {
            int j2 = this.scaledHeight - 20;
            int k2 = handside == HandSide.RIGHT ? i + 91 + 6 : i - 91 - 52;

            this.mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            int l1 = (int) (f * 19F);
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            this.blit(k2, j2, 0, 94, 18, 18);
            this.blit(k2, j2 + 18 - l1, 18, 112 - l1, 18, l1);
        }
    }

    @Inject(method = "renderAttackIndicator()V", at = @At(value = "FIELD", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/player/ClientPlayerEntity;", slice = "Lnet/minecraft/client/settings/AttackIndicatorStatus;CROSSHAIR:Lnet/minecraft/client/settings/AttackIndicatorStatus;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void renderCrossHair(CallbackInfo ci) {
        PlayerEntity player = this.mc.player;
        if (player == null) return; //Miss!

        ItemStack offhand = player.getHeldItemOffhand();
        if(!OffHandHandler.canSwingHand(player, Hand.OFF_HAND)) return; //YOINK!
        ItemStack mainHand = player.getHeldItemMainhand();
        OffHandHandler.makeActive(player, offhand, mainHand);
        int ticksSinceLastSwingOff = Capabilities.offHand(player).map(c -> c.ticksSinceLastSwing).orElse(0);
        int ticksSinceLastSwingMain = player.ticksSinceLastSwing;
        player.ticksSinceLastSwing = ticksSinceLastSwingOff;
        float f = player.getCooledAttackStrength(0F);
        float cooldown = player.getCooldownPeriod();
        player.ticksSinceLastSwing = ticksSinceLastSwingMain;
        OffHandHandler.makeInactive(player, offhand, mainHand);

        boolean flag = false;
        if (this.mc.pointedEntity != null && this.mc.pointedEntity instanceof LivingEntity && f >= 1.0F) {
            flag = cooldown > 5.0F;
            flag = flag & this.mc.pointedEntity.isAlive();
        }

        int j = this.scaledHeight / 2 - 13;
        int k = this.scaledWidth / 2 - 8;
        if (flag) {
            this.blit(k, j, 68, 94, 16, 16);
        } else if (f < 1.0F) {
            int l = (int) (f * 17.0F);
            this.blit(k, j, 36, 94, 16, 4);
            this.blit(k, j, 52, 94, l, 4);
        }
    }
}
