package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.api.capability.OffHandCapability;
import arekkuusu.offhandcombat.common.handler.OffHandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SwingEntityMixin extends Entity {

    @Shadow
    public boolean isSwingInProgress;
    @Shadow
    public int swingProgressInt;
    @Shadow
    public Hand swingingHand;
    public boolean isSwingInProgressPre;
    public int swingProgressIntPre;
    public Hand swingingHandPre;

    public SwingEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "swing(Lnet/minecraft/util/Hand;Z)V", at = @At(target = "Lnet/minecraft/entity/LivingEntity;isSwingInProgress:Z", value = "FIELD", ordinal = 0, opcode = 180))
    public boolean isSwingInProgress(LivingEntity livingEntity, Hand p_226292_1_) {
        OffHandCapability c = Capabilities.offHand(livingEntity).filter(cc -> cc.isActive).orElse(null);
        return c != null ? (this.isSwingInProgress ? (this.swingingHand != p_226292_1_ ? c.isSwingInProgress : this.isSwingInProgress) : this.isSwingInProgress) : this.isSwingInProgress;
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "swing(Lnet/minecraft/util/Hand;Z)V", at = @At(target = "Lnet/minecraft/entity/LivingEntity;swingProgressInt:I", value = "FIELD", ordinal = 0, opcode = 180))
    public int isSwingInProgressIntA(LivingEntity livingEntity, Hand p_226292_1_) {
        OffHandCapability c = Capabilities.offHand(livingEntity).filter(cc -> cc.isActive).orElse(null);
        return c != null ? (this.isSwingInProgress ? (this.swingingHand != p_226292_1_ ? c.swingProgressInt : this.swingProgressInt) : this.swingProgressInt) : this.swingProgressInt;
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "swing(Lnet/minecraft/util/Hand;Z)V", at = @At(target = "Lnet/minecraft/entity/LivingEntity;swingProgressInt:I", value = "FIELD", ordinal = 1, opcode = 180))
    public int isSwingInProgressIntB(LivingEntity livingEntity, Hand p_226292_1_) {
        OffHandCapability c = Capabilities.offHand(livingEntity).filter(cc -> cc.isActive).orElse(null);
        return c != null ? (this.isSwingInProgress ? (this.swingingHand != p_226292_1_ ? c.swingProgressInt : this.swingProgressInt) : this.swingProgressInt) : this.swingProgressInt;
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "swing(Lnet/minecraft/util/Hand;Z)V", at = @At(target = "Lnet/minecraft/entity/LivingEntity;swingProgressInt:I", value = "FIELD", ordinal = 0, opcode = 181))
    public void setSwingProgressInt(LivingEntity livingEntity, int swingProgressInt, Hand handIn, boolean p_226292_2_) {
        isSwingInProgressPre = this.isSwingInProgress;
        swingProgressIntPre = this.swingProgressInt;
        swingingHandPre = this.swingingHand;
        OffHandCapability c = Capabilities.offHand(livingEntity).filter(cc -> cc.isActive).orElse(null);
        if (c != null && this.isSwingInProgressPre && this.swingProgressIntPre < this.getArmSwingAnimationEnd() / 2 && this.swingingHandPre != handIn) {
            c.swingProgressInt = -1;
        } else {
            livingEntity.swingProgressInt = swingProgressInt;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "swing(Lnet/minecraft/util/Hand;Z)V", at = @At(target = "Lnet/minecraft/entity/LivingEntity;isSwingInProgress:Z", value = "FIELD", ordinal = 0, opcode = 181))
    public void setSwingInProgress(LivingEntity livingEntity, boolean isSwingInProgress, Hand handIn, boolean p_226292_2_) {
        OffHandCapability c = Capabilities.offHand(livingEntity).filter(cc -> cc.isActive).orElse(null);
        if (c != null && this.isSwingInProgressPre && this.swingProgressIntPre < this.getArmSwingAnimationEnd() / 2 && this.swingingHandPre != handIn) {
            c.isSwingInProgress = true;
        } else {
            livingEntity.isSwingInProgress = isSwingInProgress;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Redirect(method = "swing(Lnet/minecraft/util/Hand;Z)V", at = @At(target = "Lnet/minecraft/entity/LivingEntity;swingingHand:Lnet/minecraft/util/Hand;", value = "FIELD", ordinal = 0, opcode = 181))
    public void setSwingProgressInt(LivingEntity livingEntity, Hand swingingHand, Hand handIn, boolean p_226292_2_) {
        OffHandCapability c = Capabilities.offHand(livingEntity).filter(cc -> cc.isActive).orElse(null);
        if (c != null && this.isSwingInProgressPre && this.swingProgressIntPre < this.getArmSwingAnimationEnd() / 2 && this.swingingHandPre != handIn) {
            c.swingingHand = handIn;
        } else {
            livingEntity.swingingHand = swingingHand;
        }
    }

    @Inject(method = "updateArmSwingProgress()V", at = @At(value = "HEAD"))
    public void updateArmSwingProgress(CallbackInfo ci) {
        Capabilities.offHand(this).filter(cc -> cc.isActive).ifPresent(c -> {
            int i = this.getArmSwingAnimationEnd();
            if (c.isSwingInProgress) {
                ++c.swingProgressInt;
                if (c.swingProgressInt >= i) {
                    c.swingProgressInt = 0;
                    c.isSwingInProgress = false;
                }
            } else {
                c.swingProgressInt = 0;
            }

            c.prevSwingProgress = c.swingProgress;
            c.swingProgress = (float) c.swingProgressInt / (float) i;
        });
    }

    @Inject(method = "resetActiveHand()V", at = @At(value = "HEAD"))
    public void resetActiveHand(CallbackInfo ci) {
        if (isHandActive()) {
            Capabilities.offHand(this).filter(cc -> cc.isActive).ifPresent(c -> {
                c.ticksSinceLastActiveStack = 0;
                c.handOfLastActiveStack = getActiveHand();
            });
        }
    }

    @Shadow
    public abstract boolean isHandActive();

    @Shadow
    public abstract Hand getActiveHand();

    @Shadow
    protected abstract int getArmSwingAnimationEnd();
}
