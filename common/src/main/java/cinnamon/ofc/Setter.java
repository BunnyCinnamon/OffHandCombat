package cinnamon.ofc;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;

public interface Setter {
    void setPlayer(ServerPlayer serverPlayer);
    void setPacket(ServerboundInteractPacket serverboundInteractPacket);
}
