package cinnamon.ofc.mixin;

import cinnamon.ofc.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class ModelMixin extends EntityModel<LivingEntity> {

    @Final
    @Shadow
    public ModelPart body;
    @Final
    @Shadow
    public ModelPart head;

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"))
    public void setupAttackAnimation(LivingEntity livingEntity, float f, CallbackInfo ci) {
        Mod.Data data = Mod.get(livingEntity);
        HumanoidArm handside = this.getAttackArm(livingEntity);
        ModelPart modelrenderer = this.getArm(handside.getOpposite());
        float swingProgress = getSwingProgress(data, Minecraft.getInstance().getDeltaFrameTime());
        float f1 = 1.0F - swingProgress;
        f1 = f1 * f1;
        f1 = f1 * f1;
        f1 = 1.0F - f1;
        double f2 = Math.sin(f1 * (float) Math.PI);
        double f3 = Math.sin(swingProgress * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
        modelrenderer.xRot = (float) ((double) modelrenderer.xRot - (f2 * 1.2D + f3));
        modelrenderer.yRot += this.body.yRot * 2.0F;
        modelrenderer.zRot += Math.sin(swingProgress * (float) Math.PI) * -0.4F;
    }

    public float getSwingProgress(Mod.Data data, float partialTickTime) {
        float f = data.attackAnim - data.attackAnim_;
        if (f < 0.0F) {
            ++f;
        }

        return data.attackAnim_ + f * partialTickTime;
    }

    @Shadow
    protected abstract ModelPart getArm(HumanoidArm arg);

    @Shadow
    protected abstract HumanoidArm getAttackArm(LivingEntity arg);
}
