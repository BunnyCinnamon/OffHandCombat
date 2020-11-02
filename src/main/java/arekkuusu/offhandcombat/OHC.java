package arekkuusu.offhandcombat;

import arekkuusu.offhandcombat.api.capability.OffHandCapability;
import arekkuusu.offhandcombat.common.network.OHCPacketHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OHC.MOD_ID)
public class OHC {

    //Useful names
    public static final String MOD_ID = "offhandcombat";
    public static final String MOD_NAME = "Off Hand Combat";

    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public OHC() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OHCConfig.Holder.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OHCConfig.Holder.COMMON_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFingerprintViolation);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfigEvent);
    }

    public void setup(final FMLCommonSetupEvent event) {
        OffHandCapability.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        OHCPacketHandler.init();
    }

    public void onFingerprintViolation(final FMLFingerprintViolationEvent event) {
        LOG.warn("Invalid fingerprint detected!");
    }

    public void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == OHCConfig.Holder.CLIENT_SPEC) {
            OHCConfig.Setup.client(config);
            LOG.debug("Baked client config");
        } else if (config.getSpec() == OHCConfig.Holder.COMMON_SPEC) {
            OHCConfig.Setup.server(config);
            LOG.debug("Baked server config");
        }
    }
}
