package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderer.class)
public class ReEquipOptifine {
    
    @Shadow
    private float equippedProgressOffHand;
    @Shadow
    private ItemStack itemStackOffHand;
    public boolean requipO;

    @Inject(method = "tick()V", at = @At(target = "Lnet/minecraft/item/ItemStack;areItemStacksEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0))
    public void getReEquip(CallbackInfo ci) {
        PlayerEntity player = Minecraft.getInstance().player;
        ItemStack itemstack1 = player.getHeldItemOffhand();
        this.requipO = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.itemStackOffHand, itemstack1, -1);
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
}
