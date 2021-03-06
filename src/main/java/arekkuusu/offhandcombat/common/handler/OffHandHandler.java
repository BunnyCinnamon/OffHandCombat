package arekkuusu.offhandcombat.common.handler;

import arekkuusu.offhandcombat.OHCConfig;
import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.api.capability.OffHandCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class OffHandHandler {

    public static boolean canUseOffhand(LivingEntity player) {
        OffHandCapability capability = player.getCapability(Capabilities.OFF_HAND, null).orElse(null);
        return capability != null && capability.isActive;
    }

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
            int halfTick = (int) (OHCConfig.Runtime.attackCooldownSetAfterSwing * player.getCooldownPeriod());
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
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        return item.getAttributeModifiers(
                hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND,
                stack
        ).containsKey(Attributes.field_233823_f_) || item.getAttributeModifiers(
                EquipmentSlotType.MAINHAND,
                stack
        ).containsKey(Attributes.field_233823_f_);
    }

    public static void makeActive(PlayerEntity playerIn, ItemStack offhand, ItemStack mainHand) {
        playerIn.func_233645_dx_().func_233785_a_(mainHand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
        playerIn.func_233645_dx_().func_233785_a_(offhand.getAttributeModifiers(EquipmentSlotType.OFFHAND));
        playerIn.func_233645_dx_().func_233793_b_(offhand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
    }

    public static void makeInactive(PlayerEntity playerIn, ItemStack offhand, ItemStack mainHand) {
        playerIn.func_233645_dx_().func_233785_a_(mainHand.getAttributeModifiers(EquipmentSlotType.OFFHAND));
        playerIn.func_233645_dx_().func_233785_a_(offhand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
        playerIn.func_233645_dx_().func_233793_b_(mainHand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
    }

    public static void setItemStackToSlot(PlayerEntity playerIn, EquipmentSlotType slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlotType.MAINHAND) {
            playerIn.inventory.mainInventory.set(playerIn.inventory.currentItem, stack);
        } else if (slotIn == EquipmentSlotType.OFFHAND) {
            playerIn.inventory.offHandInventory.set(0, stack);
        }
    }
}