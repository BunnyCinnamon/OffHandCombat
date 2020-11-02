package arekkuusu.offhandcombat.api.capability;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Capabilities {

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> Capability<T> empty() {
        return null;
    }

    @CapabilityInject(OffHandCapability.class)
    public static final Capability<OffHandCapability> OFF_HAND = empty();

    public static LazyOptional<OffHandCapability> offHand(@Nullable Entity entity) {
        return entity != null ? entity.getCapability(OFF_HAND, null) : LazyOptional.empty();
    }
}
