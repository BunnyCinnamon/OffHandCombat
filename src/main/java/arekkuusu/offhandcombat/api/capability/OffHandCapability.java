package arekkuusu.offhandcombat.api.capability;

import arekkuusu.offhandcombat.OHC;
import arekkuusu.offhandcombat.api.OFCAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OffHandCapability implements ICapabilitySerializable<CompoundNBT>, Capability.IStorage<OffHandCapability> {

    //Off-hand
    public int ticksSinceLastSwing;
    //Any-hand
    public Hand swingingHand;
    public float swingProgress;
    public int swingProgressInt;
    public float prevSwingProgress;
    public boolean isSwingInProgress;
    public int ticksSinceLastActiveStack;
    public Hand handOfLastActiveStack;
    //Toggle
    public boolean isActive = OFCAPI.isOffhandSwingableByDefault;

    public static void init() {
        CapabilityManager.INSTANCE.register(OffHandCapability.class, new OffHandCapability(), OffHandCapability::new);
        MinecraftForge.EVENT_BUS.register(new OffHandCapability.Handler());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.OFF_HAND.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.OFF_HAND.getStorage().writeNBT(Capabilities.OFF_HAND, this, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.OFF_HAND.getStorage().readNBT(Capabilities.OFF_HAND, this, null, nbt);
    }

    @Nullable
    @Override
    public INBT writeNBT(Capability<OffHandCapability> capability, OffHandCapability instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("isActive", instance.isActive);
        return nbt;
    }

    @Override
    public void readNBT(Capability<OffHandCapability> capability, OffHandCapability instance, Direction side, INBT nbt) {
        instance.isActive = ((CompoundNBT) nbt).getBoolean("isActive");
    }

    public static class Handler {
        private static final ResourceLocation KEY = new ResourceLocation(OHC.MOD_ID, "off_hand");

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof PlayerEntity)
                event.addCapability(KEY, Capabilities.OFF_HAND.getDefaultInstance());
        }

        @SubscribeEvent
        public void clonePlayer(PlayerEvent.Clone event) {
            event.getPlayer().getCapability(Capabilities.OFF_HAND, null).ifPresent(first -> {
                event.getOriginal().getCapability(Capabilities.OFF_HAND, null).ifPresent(second -> {
                    first.deserializeNBT(second.serializeNBT());
                });
            });
        }
    }
}
