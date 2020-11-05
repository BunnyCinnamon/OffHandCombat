package arekkuusu.offhandcombat.mixin;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedModel.class)
public abstract class ModelSwingMixin {

    @Shadow
    public ModelRenderer bipedBody;
    @Shadow
    public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(target = "Lnet/minecraft/client/renderer/entity/model/BipedModel;getMainHand(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/HandSide;", value = "INVOKE_ASSIGN", shift = At.Shift.BEFORE, ordinal = 0))
    public void swingHand(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        Capabilities.offHand(entityIn).ifPresent(c -> {
            HandSide handside = this.getMainHand(entityIn);
            ModelRenderer modelrenderer = this.getArmForSide(handside.opposite());
            float f1 = 1.0F - c.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float)Math.PI);
            float f3 = MathHelper.sin(c.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(c.swingProgress * (float)Math.PI) * -0.4F;
        });
    }

    @Shadow
    protected abstract ModelRenderer getArmForSide(HandSide side);

    @Shadow
    protected abstract HandSide getMainHand(LivingEntity entityIn);
}
