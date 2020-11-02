package arekkuusu.offhandcombat.common.core;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfiguration("assets/offhandcombat/offhandcombat.mixins.json");
    }
}
