package arekkuusu.offhandcombat.common.command;

import arekkuusu.offhandcombat.api.capability.Capabilities;
import arekkuusu.offhandcombat.common.network.OHCPacketHandler;
import arekkuusu.offhandcombat.common.network.PacketOffHandAttackToggle;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommandToggle {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> argument = Commands.literal("offhandcombat").requires(commandSource -> commandSource.hasPermissionLevel(2));
        String[] args = {"toggle", "enable", "disable"};
        for (String arg : args) {
            argument.then(Commands.literal(arg).executes(context -> export(context.getSource().asPlayer(), arg))
                    .then(Commands.argument("target", EntityArgument.player()).executes(context -> export(EntityArgument.getPlayer(context, "target"), arg)))
            );
        }
        dispatcher.register(argument);
    }

    public static int export(PlayerEntity player, String argument) {
        player.getCapability(Capabilities.OFF_HAND, null).ifPresent(capability -> {
            switch (argument) {
                case "toggle":
                    capability.isActive = !capability.isActive;
                    OHCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketOffHandAttackToggle(capability.isActive));
                    break;
                case "enable":
                    capability.isActive = true;
                    OHCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketOffHandAttackToggle(capability.isActive));
                    break;
                case "disable":
                    capability.isActive = false;
                    OHCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketOffHandAttackToggle(capability.isActive));
                    break;
            }
        });
        return 0;
    }
}
