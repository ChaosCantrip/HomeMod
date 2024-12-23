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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
            ServerLevel level = (ServerLevel) player.getServer().overworld();
            HomeSavedData homeData = HomeSavedData.get(level);

            ResourceKey<Level> dimension = player.level().dimension();
            BlockPos pos = player.blockPosition();

            Location home = new Location(pos, dimension.location().toString());

            homeData.setHome(player.getUUID(), home);

            player.sendSystemMessage(Component.literal("Set Home location."));
        }
        return Command.SINGLE_SUCCESS;
    }

}
