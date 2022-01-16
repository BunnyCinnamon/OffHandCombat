package cinnamon.ofc.forge;

import dev.architectury.platform.forge.EventBuses;
import cinnamon.ofc.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@net.minecraftforge.fml.common.Mod(Mod.MOD_ID)
public class ModForge {
    public ModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Mod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Mod.init();
    }
}
