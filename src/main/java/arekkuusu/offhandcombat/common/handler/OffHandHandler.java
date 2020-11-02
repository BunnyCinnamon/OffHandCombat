package arekkuusu.offhandcombat.common.handler;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.Hand;

public class OffHandHandler {

    public static void attackEntity(PlayerEntity player, Entity targetEntity) {
        ItemStack offhand = player.getHeldItemOffhand();
        ItemStack mainHand = player.getHeldItemMainhand();
        int ticksSinceLastSwingOff = Capabilities.offHand(player).map(c -> c.ticksSinceLastSwing).orElse(0);
        int ticksSinceLastSwingMain = player.ticksSinceLastSwing;

        //Switch items
        setItemStackToSlot(player, EquipmentSlotType.MAINHAND, offhand);
        setItemStackToSlot(player, EquipmentSlotType.OFFHAND, mainHand);
        makeActive(player, offhand, mainHand);

        //Swing
        player.ticksSinceLastSwing = ticksSinceLastSwingOff;
        player.attackTargetEntityWithCurrentItem(targetEntity);
        player.ticksSinceLastSwing = ticksSinceLastSwingMain;

        //Reset Swing to half on main hand and full on off hand
        Capabilities.offHand(player).ifPresent(c -> c.ticksSinceLastSwing = 0);
        if (canSwingHand(player, Hand.MAIN_HAND)) {
            int halfTick = (int) (0.5F * player.getCooldownPeriod());
            if (ticksSinceLastSwingMain > halfTick) {
                player.ticksSinceLastSwing = halfTick;
            }
        }

        //Switch back items
        setItemStackToSlot(player, EquipmentSlotType.OFFHAND, offhand);
        setItemStackToSlot(player, EquipmentSlotType.MAINHAND, mainHand);
        makeInactive(player, offhand, mainHand);
    }

    public static boolean canSwingHand(PlayerEntity player, Hand hand) {
        Item item = player.getHeldItem(hand).getItem();
        return item instanceof TieredItem;
    }

    public static void makeActive(PlayerEntity playerIn, ItemStack offhand, ItemStack mainHand) {
        playerIn.getAttributes().removeAttributeModifiers(mainHand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
        playerIn.getAttributes().removeAttributeModifiers(offhand.getAttributeModifiers(EquipmentSlotType.OFFHAND));
        playerIn.getAttributes().applyAttributeModifiers(offhand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
    }

    public static void makeInactive(PlayerEntity playerIn, ItemStack offhand, ItemStack mainHand) {
        playerIn.getAttributes().removeAttributeModifiers(mainHand.getAttributeModifiers(EquipmentSlotType.OFFHAND));
        playerIn.getAttributes().removeAttributeModifiers(offhand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
        playerIn.getAttributes().applyAttributeModifiers(mainHand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
    }

    public static void setItemStackToSlot(PlayerEntity playerIn, EquipmentSlotType slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlotType.MAINHAND) {
            playerIn.inventory.mainInventory.set(playerIn.inventory.currentItem, stack);
        } else if (slotIn == EquipmentSlotType.OFFHAND) {
            playerIn.inventory.offHandInventory.set(0, stack);
        }
    }
}
