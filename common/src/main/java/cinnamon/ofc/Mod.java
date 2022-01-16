package cinnamon.ofc;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mod {

    public static final String MOD_ID = "offhandcombat";

    public static Map<UUID, Data> swing = new HashMap<>();

    public static void init() {
        TickEvent.ServerLevelTick.PLAYER_POST.register(instance -> {
            Data data = Mod.get(instance);
            data.missTime--;
            data.doOverride = false;
            data.ticksSinceLastActiveStack++;
        });
        String s = HandPlatform.getConfigDirectory().toAbsolutePath().normalize().toString();
    }

    public static Data get(Entity entity) {
        if(!swing.containsKey(entity.getUUID())) {
            swing.put(entity.getUUID(), new Data());
        }
        return swing.get(entity.getUUID());
    }

    public static class Data {
        //
        public boolean doOverride;
        //
        public int missTime;
        //
        public int swingTime;
        public boolean swinging;
        public float attackAnim;
        public float attackAnim_;
        public InteractionHand swingingArm;
        //
        public int ticksSinceLastActiveStack;
        public InteractionHand handOfLastActiveStack;
    }
}
