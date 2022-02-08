package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import cinnamon.ofc.Mod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

    @Redirect(method = "handleKeybinds()V", at = @At(target = "Lnet/minecraft/client/Minecraft;startUseItem()V", value = "INVOKE", ordinal = 0))
    public void processKeyBinds(Minecraft minecraft) {
        if (!this.player.isHandsBusy() && !this.player.isCrouching() && HandPlatform.canUseOffhand(player) && HandPlatform.canSwingHand(this.player, InteractionHand.OFF_HAND)) {
            Mod.Data data = Mod.get(this.player);
            if (data.missTime <= 0 && this.hitResult != null) {
                switch (this.hitResult.getType()) {
                    case ENTITY:
                        data.doOverride = true;
                        this.gameMode.attack(this.player, ((EntityHitResult) this.hitResult).getEntity());
                        this.player.swing(InteractionHand.OFF_HAND);
                        break;
                    case BLOCK:
                        break;
                    case MISS:
                        if (Objects.requireNonNull(this.gameMode).hasMissTime()) {
                            data.missTime = 10;
                        }
                        this.player.swing(InteractionHand.OFF_HAND);
                        break;
                }
            }
        }
        //Fallback
        startUseItem();
    }

    @Shadow
    protected abstract void startUseItem();
}
