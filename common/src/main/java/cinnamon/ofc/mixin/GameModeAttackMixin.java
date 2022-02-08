package cinnamon.ofc.mixin;

import cinnamon.ofc.HandPlatform;
import cinnamon.ofc.Mod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class GameModeAttackMixin {

    @Inject(method = "attack", at = @At(target = "Lnet/minecraft/world/entity/player/Player;attack(Lnet/minecraft/world/entity/Entity;)V", value = "INVOKE"), cancellable = true)
    public void attack(Player player, Entity entity, CallbackInfo ci) {
        if(Mod.get(player).doOverride) {
            HandPlatform.attack(player, entity);
            ci.cancel();
        }
    }
}
