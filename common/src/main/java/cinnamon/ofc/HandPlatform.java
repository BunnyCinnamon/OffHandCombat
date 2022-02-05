package cinnamon.ofc;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

public class HandPlatform {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }

    public static boolean canUseOffhand(Entity entity) {
        return entity instanceof Player;
    }

    public static void attack(Player player, Entity targetEntity) {
        Mod.get(player).doOverride = false;

        ItemStack offhand = player.getOffhandItem();
        ItemStack mainHand = player.getMainHandItem();
        Mod.Data data = Mod.get(player);
        int ticksSinceLastSwingOff = data.attackStrengthTicker;
        int ticksSinceLastSwingMain = player.attackStrengthTicker;

        //Switch items
        setItemStackToSlot(player, EquipmentSlot.MAINHAND, offhand);
        setItemStackToSlot(player, EquipmentSlot.OFFHAND, mainHand);
        makeActive(player, offhand, mainHand);

        //Swing
        player.attackStrengthTicker = ticksSinceLastSwingOff;
        player.attack(targetEntity);
        player.attackStrengthTicker = ticksSinceLastSwingMain;

        //Reset Swing to half on main hand and full on off hand
        data.attackStrengthTicker = 0;
        if (canSwingHand(player, InteractionHand.MAIN_HAND)) {
            int halfTick = (int) (Config.Runtime.attackTimeoutAfterSwing * player.getCurrentItemAttackStrengthDelay());
            if (ticksSinceLastSwingMain > halfTick) {
                player.attackStrengthTicker = halfTick;
            }
        }

        //Switch back items
        setItemStackToSlot(player, EquipmentSlot.OFFHAND, offhand);
        setItemStackToSlot(player, EquipmentSlot.MAINHAND, mainHand);
        makeInactive(player, offhand, mainHand);
    }

    public static void resetAttackStrengthTickerMainHand(Player player) {
        if (canSwingHand(player, InteractionHand.OFF_HAND)) {
            Mod.Data data = Mod.get(player);
            int ticksSinceLastSwingOff = data.attackStrengthTicker;
            ItemStack offhand = player.getOffhandItem();
            ItemStack mainHand = player.getMainHandItem();

            HandPlatform.makeActive(player, offhand, mainHand);
            int halfTick = (int) (Config.Runtime.attackTimeoutAfterSwing * player.getCurrentItemAttackStrengthDelay());
            HandPlatform.makeInactive(player, offhand, mainHand);

            if (ticksSinceLastSwingOff > halfTick) {
                data.attackStrengthTicker = halfTick;
            }
        }
    }

    public static void resetAttackStrengthTickerOffHand(Player player) {
        Mod.Data data = Mod.get(player);
        data.attackStrengthTicker = 0;
        if (canSwingHand(player, InteractionHand.MAIN_HAND)) {
            int halfTick = (int) (Config.Runtime.attackTimeoutAfterSwing * player.getCurrentItemAttackStrengthDelay());
            if (player.attackStrengthTicker > halfTick) {
                player.attackStrengthTicker = halfTick;
            }
        }
    }

    public static boolean canSwingHand(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return stack.getAttributeModifiers(
                hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
        ).containsKey(Attributes.ATTACK_DAMAGE) || stack.getAttributeModifiers(
                EquipmentSlot.MAINHAND
        ).containsKey(Attributes.ATTACK_DAMAGE);
    }

    public static void makeActive(Player playerIn, ItemStack offhand, ItemStack mainHand) {
        playerIn.getAttributes().removeAttributeModifiers(mainHand.getAttributeModifiers(EquipmentSlot.MAINHAND));
        playerIn.getAttributes().removeAttributeModifiers(offhand.getAttributeModifiers(EquipmentSlot.OFFHAND));
        playerIn.getAttributes().addTransientAttributeModifiers(offhand.getAttributeModifiers(EquipmentSlot.MAINHAND));
        playerIn.getAttributes().addTransientAttributeModifiers(mainHand.getAttributeModifiers(EquipmentSlot.OFFHAND));
    }

    public static void makeInactive(Player playerIn, ItemStack offhand, ItemStack mainHand) {
        playerIn.getAttributes().removeAttributeModifiers(mainHand.getAttributeModifiers(EquipmentSlot.OFFHAND));
        playerIn.getAttributes().removeAttributeModifiers(offhand.getAttributeModifiers(EquipmentSlot.MAINHAND));
        playerIn.getAttributes().addTransientAttributeModifiers(mainHand.getAttributeModifiers(EquipmentSlot.MAINHAND));
        playerIn.getAttributes().addTransientAttributeModifiers(offhand.getAttributeModifiers(EquipmentSlot.OFFHAND));
    }

    public static void setItemStackToSlot(Player playerIn, EquipmentSlot slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlot.MAINHAND) {
            playerIn.getInventory().items.set(playerIn.getInventory().selected, stack);
        } else if (slotIn == EquipmentSlot.OFFHAND) {
            playerIn.getInventory().offhand.set(0, stack);
        }
    }
}
