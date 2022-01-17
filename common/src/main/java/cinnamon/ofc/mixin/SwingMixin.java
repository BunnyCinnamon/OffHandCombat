package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import cinnamon.ofc.Mod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SwingMixin extends Entity {

    @Shadow
    public boolean swinging;
    @Shadow
    public int swingTime;
    @Shadow
    public InteractionHand swingingArm;
    //
    @Unique
    public boolean swinging_temp;
    @Unique
    public int swingTime_temp;
    @Unique
    public InteractionHand swingingArm_temp;

    public SwingMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;swinging:Z", value = "FIELD", ordinal = 0, opcode = 180))
    public boolean swinging(LivingEntity livingEntity, InteractionHand interactionHand, boolean bl) {
        if(!HandPlatform.canUseOffhand(livingEntity)) return this.swinging;

        Mod.Data data = Mod.get(livingEntity);
        return this.swinging ? (this.swingingArm != interactionHand ? data.swinging : this.swinging) : this.swinging;
    }

    @Redirect(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;swingTime:I", value = "FIELD", ordinal = 0, opcode = 180))
    public int swingingTime(LivingEntity livingEntity, InteractionHand interactionHand, boolean bl) {
        if(!HandPlatform.canUseOffhand(livingEntity)) return this.swingTime;

        Mod.Data data = Mod.get(livingEntity);
        return this.swinging ? (this.swingingArm != interactionHand ? data.swingTime : this.swingTime) : this.swingTime;
    }

    @Redirect(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;swingTime:I", value = "FIELD", ordinal = 1, opcode = 180))
    public int swingingTime_(LivingEntity livingEntity, InteractionHand interactionHand, boolean bl) {
        if(!HandPlatform.canUseOffhand(livingEntity)) return this.swingTime;

        Mod.Data data = Mod.get(livingEntity);
        return this.swinging ? (this.swingingArm != interactionHand ? data.swingTime : this.swingTime) : this.swingTime;
    }

    @Redirect(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;swingTime:I", value = "FIELD", ordinal = 0, opcode = 181))
    public void setSwingTime(LivingEntity livingEntity, int swingTime, InteractionHand interactionHand, boolean bl) {
        if(!HandPlatform.canUseOffhand(livingEntity)) {
            livingEntity.swingTime = swingTime;
            return;
        }

        this.swinging_temp = this.swinging;
        this.swingTime_temp = this.swingTime;
        this.swingingArm_temp = this.swingingArm;
        Mod.Data data = Mod.get(livingEntity);
        if (this.swinging_temp && this.swingTime_temp < this.getCurrentSwingDuration() / 2 && this.swingingArm_temp != interactionHand) {
            data.swingTime = -1;
        } else {
            livingEntity.swingTime = swingTime;
        }
    }

    @Redirect(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;swinging:Z", value = "FIELD", ordinal = 0, opcode = 181))
    public void setSwinging(LivingEntity livingEntity, boolean swinging, InteractionHand interactionHand, boolean bl) {
        if(!HandPlatform.canUseOffhand(livingEntity)) {
            livingEntity.swinging = swinging;
            return;
        }

        Mod.Data data = Mod.get(livingEntity);
        if (this.swinging_temp && this.swingTime_temp < this.getCurrentSwingDuration() / 2 && this.swingingArm_temp != interactionHand) {
            data.swinging = true;
        } else {
            livingEntity.swinging = swinging;
        }
    }

    @Redirect(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;swingingArm:Lnet/minecraft/world/InteractionHand;", value = "FIELD", ordinal = 0, opcode = 181))
    public void setSwingingArm(LivingEntity livingEntity, InteractionHand swingingArm, InteractionHand interactionHand, boolean bl) {
        if(!HandPlatform.canUseOffhand(livingEntity)) {
            livingEntity.swingingArm = interactionHand;
            return;
        }

        Mod.Data data = Mod.get(livingEntity);
        if (this.swinging_temp && this.swingTime_temp < this.getCurrentSwingDuration() / 2 && this.swingingArm_temp != interactionHand) {
            data.swingingArm = interactionHand;
        } else {
            livingEntity.swingingArm = interactionHand;
        }
    }

    @Inject(method = "updateSwingTime()V", at = @At(value = "HEAD"))
    public void updateArmSwingProgress(CallbackInfo ci) {
        if(!HandPlatform.canUseOffhand(this)) {
            return;
        }

        Mod.Data data = Mod.get(this);
        int i = this.getCurrentSwingDuration();
        if (data.swinging) {
            ++data.swingTime;
            if (data.swingTime >= i) {
                data.swingTime = 0;
                data.swinging = false;
            }
        } else {
            data.swingTime = 0;
        }

        data.attackAnim_ = data.attackAnim;
        data.attackAnim = (float) data.swingTime / (float) i;
    }

    @Inject(method = "stopUsingItem()V", at = @At(value = "HEAD"))
    public void resetActiveHand(CallbackInfo ci) {
        if(!HandPlatform.canUseOffhand(this)) {
            return;
        }

        if (isUsingItem()) {
            Mod.Data data = Mod.get(this);
            data.ticksSinceLastActiveStack = 0;
            data.handOfLastActiveStack = getUsedItemHand();
        }
    }

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract InteractionHand getUsedItemHand();

    @Shadow
    protected abstract int getCurrentSwingDuration();
}
