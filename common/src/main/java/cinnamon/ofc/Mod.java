package cinnamon.ofc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mod {

    public static final String MOD_ID = "offhandcombat";

    public static Map<UUID, Data> swing = new HashMap<>();
    public static Map<UUID, Data> swingLocal = new HashMap<>();

    public static void init() {
        TickEvent.ServerLevelTick.PLAYER_POST.register(instance -> {
            Data data = Mod.get(instance);
            if(!instance.isLocalPlayer()) {
                data.missTime--;
                data.attackStrengthTicker++;
                data.ticksSinceLastActiveStack++;
            }
        });
        String directory = HandPlatform.getConfigDirectory().toAbsolutePath().normalize().toString();
        Mod.makeConfig(directory);
        Mod.readConfig(directory);
    }

    public static void makeConfig(String location) {
        File file = new File(location + "/ofc.json");
        if (!file.exists()) {
            try {
                boolean newFile = file.createNewFile();
                if (newFile) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("attackTimeoutAfterSwing", new JsonPrimitive(Config.Runtime.attackTimeoutAfterSwing));

                    ObjectMapper objectMapper = ObjectMapper.create();
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(objectMapper.writeValueAsString(jsonObject));
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readConfig(String location) {
        File file = new File(location + "/ofc.json");
        if (file.exists()) {
            try {
                JsonObject jsonObject = (JsonObject) JsonParser.parseReader(new FileReader(file));
                JsonElement attackTimeoutAfterSwing = jsonObject.get("attackTimeoutAfterSwing");
                Config.Runtime.attackTimeoutAfterSwing = attackTimeoutAfterSwing.getAsDouble();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Data get(Player entity) {
        if (entity.isLocalPlayer()) {
            if (!swingLocal.containsKey(entity.getUUID())) {
                swingLocal.put(entity.getUUID(), new Data());
            }
            return swingLocal.get(entity.getUUID());
        } else {
            if (!swing.containsKey(entity.getUUID())) {
                swing.put(entity.getUUID(), new Data());
            }
            return swing.get(entity.getUUID());
        }
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
        public int attackStrengthTicker;
        public InteractionHand swingingArm;
        //
        public int ticksSinceLastActiveStack;
        public InteractionHand handOfLastActiveStack;
    }
}
