package com.chaoscantrip.homemod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SetHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("sethome").executes(SetHomeCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player player) {

            String dimension = player.level().dimension().toString();
            BlockPos pos = player.blockPosition();

            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            Location home = new Location(pos, dimension);

            player.getPersistentData().put("home", Location.CODEC.encode(home, NbtOps.INSTANCE, NbtOps.INSTANCE.empty()).result().get());

            player.sendSystemMessage(Component.literal("Set Home command invocated"));
        }
        return Command.SINGLE_SUCCESS;
    }

}
