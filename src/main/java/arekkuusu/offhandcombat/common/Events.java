package arekkuusu.offhandcombat.common;

import arekkuusu.offhandcombat.OHC;
import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.common.network.OHCPacketHandler;
import arekkuusu.offhandcombat.common.network.PacketOffHandAttackToggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = OHC.MOD_ID)
public class Events {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Capabilities.offHand(event.getEntity()).ifPresent(capability -> {
            if(!capability.isActive) return;
            capability.ticksSinceLastSwing++;
            capability.ticksSinceLastActiveStack++;
        });
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            Capabilities.offHand(event.getEntity()).ifPresent(capability -> {
                OHCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getEntity()), new PacketOffHandAttackToggle(capability.isActive));
            });
        }
    }
}
