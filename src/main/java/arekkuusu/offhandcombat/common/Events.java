package arekkuusu.offhandcombat.common;

import arekkuusu.offhandcombat.OHC;
import arekkuusu.offhandcombat.api.capability.Capabilities;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OHC.MOD_ID)
public class Events {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Capabilities.offHand(event.getEntity()).ifPresent(capability -> {
            capability.ticksSinceLastSwing++;
        });
    }
}
