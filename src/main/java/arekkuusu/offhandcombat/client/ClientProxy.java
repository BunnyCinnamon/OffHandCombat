package arekkuusu.offhandcombat.client;

import arekkuusu.offhandcombat.common.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public PlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }
}