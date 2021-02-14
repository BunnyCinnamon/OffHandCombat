package arekkuusu.offhandcombat;

import arekkuusu.offhandcombat.api.capability.OffHandCapability;
import arekkuusu.offhandcombat.client.ClientProxy;
import arekkuusu.offhandcombat.common.ServerProxy;
import arekkuusu.offhandcombat.common.command.CommandToggle;
import arekkuusu.offhandcombat.common.network.OHCPacketHandler;
import arekkuusu.offhandcombat.common.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OHC.MOD_ID)
public class OHC {

    //Useful names
    public static final String MOD_ID = "offhandcombat";
    public static final String MOD_NAME = "Off Hand Combat";
    //Logger
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    private static IProxy proxy;

    public static IProxy getProxy() {
        return proxy;
    }
    //Mods that require special handling...
    public static boolean isTinkers = false;

    public OHC() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OHCConfig.Holder.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OHCConfig.Holder.COMMON_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::setupServer);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFingerprintViolation);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfigEvent);
        OHC.isTinkers = ModList.get().isLoaded("tconstruct");
    }

    public void setup(final FMLCommonSetupEvent event) {
        OffHandCapability.init();
        OHCPacketHandler.init();
    }

    public void setupServer(final FMLServerStartingEvent event) {
        CommandToggle.register(event.getServer().getCommandManager().getDispatcher());
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
