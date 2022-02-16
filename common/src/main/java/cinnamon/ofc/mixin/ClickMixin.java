package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import cinnamon.ofc.Mod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class ClickMixin {

    @Shadow
    public LocalPlayer player;
    @Shadow
    public HitResult hitResult;
    @Shadow
    public MultiPlayerGameMode gameMode;

    @Inject(method = "startUseItem", at = @At(value = "HEAD"), cancellable = true)
    public void startUseItem(CallbackInfo ci) {
        if (!this.player.isHandsBusy() && !this.player.isCrouching() && HandPlatform.canUseOffhand(player) && HandPlatform.canSwingHand(this.player, InteractionHand.OFF_HAND)) {
            Mod.Data data = Mod.get(this.player);
            if (data.missTime <= 0 && this.hitResult != null) {
                switch (this.hitResult.getType()) {
                    case ENTITY:
                        data.doOverride = true;
                        this.gameMode.attack(this.player, ((EntityHitResult) this.hitResult).getEntity());
                        this.player.swing(InteractionHand.OFF_HAND);
                        Minecraft.getInstance().options.keyUse.release();
                        break;
                    case BLOCK:
                        return;
                    case MISS:
                        ItemStack stack = this.player.getMainHandItem();
                        UseAnim useAnimation = stack.getUseAnimation();
                        if(useAnimation != UseAnim.NONE) {
                            return;
                        }

                        if (Objects.requireNonNull(this.gameMode).hasMissTime()) {
                            data.missTime = 10;
                        }
                        this.player.swing(InteractionHand.OFF_HAND);
                        Minecraft.getInstance().options.keyUse.release();
                        break;
                }
            }
            ci.cancel();
        }
    }
}
