package arekkuusu.offhandcombat;

import arekkuusu.offhandcombat.api.OFCAPI;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class OHCConfig {

    public static class Common {

        public final ForgeConfigSpec.DoubleValue attackCooldownSetAfterSwing;
        public final ForgeConfigSpec.BooleanValue isOffhandSwingableByDefault;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");
            attackCooldownSetAfterSwing = builder
                    .comment("")
                    .defineInRange("attackCooldownSetAfterSwing", 0.5D, 0D, 1D);
            isOffhandSwingableByDefault = builder
                    .comment("")
                    .define("isOffhandSwing-ableByDefault", true);
            builder.pop();
        }
    }

    public static class Client {

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                    .push("client");
            builder.pop();
        }
    }

    public static final class Holder {

        public static final Common COMMON;
        public static final ForgeConfigSpec COMMON_SPEC;

        public static final Client CLIENT;
        public static final ForgeConfigSpec CLIENT_SPEC;

        static {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON_SPEC = specPair.getRight();
            COMMON = specPair.getLeft();
        }

        static {
            final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
            CLIENT_SPEC = specPair.getRight();
            CLIENT = specPair.getLeft();
        }
    }

    public static final class Setup {

        public static void client(final ModConfig config) {

        }

        public static void server(final ModConfig config) {
            Runtime.attackCooldownSetAfterSwing = Holder.COMMON.attackCooldownSetAfterSwing.get();
            Runtime.isOffhandSwingableByDefault = Holder.COMMON.isOffhandSwingableByDefault.get();
            OFCAPI.isOffhandSwingableByDefault = Runtime.isOffhandSwingableByDefault;
        }
    }

    public static final class Runtime {

        public static double attackCooldownSetAfterSwing;
        public static boolean isOffhandSwingableByDefault;
    }
}
