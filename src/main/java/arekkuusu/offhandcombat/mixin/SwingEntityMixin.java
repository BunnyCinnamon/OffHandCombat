package arekkuusu.offhandcombat.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class SwingEntityMixin {

    @Shadow
    public boolean isSwingInProgress;
    @Shadow
    public Hand swingingHand;

    @Inject(method = "swing(Lnet/minecraft/util/Hand;Z)V", at = @At(target = "Lnet/minecraft/entity/LivingEntity;isSwingInProgress:Z", value = "FIELD", shift = At.Shift.BEFORE, ordinal = 0))
    public void isSwingInProgress(Hand p_226292_1_, boolean p_226292_2_, CallbackInfo ci) {
        this.isSwingInProgress = this.isSwingInProgress && p_226292_1_ == this.swingingHand;
    }
}
