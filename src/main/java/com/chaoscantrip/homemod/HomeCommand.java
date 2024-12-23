package com.chaoscantrip.homemod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("home").executes(HomeCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player player) {
            ServerLevel level = (ServerLevel) player.getServer().overworld();
            HomeSavedData homeData = HomeSavedData.get(level);
            Location home = homeData.getHome(player.getUUID());

            if (home != null) {
                ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(home.dimension));
                if (player.level().dimension().equals(dimensionKey)) {
                    player.teleportTo(home.pos.getX(), home.pos.getY(), home.pos.getZ());
                } else {
                    ServerLevel targetLevel = player.getServer().getLevel(dimensionKey);
                    if (targetLevel != null) {
                        player.changeDimension(targetLevel);
                        player.teleportTo(home.pos.getX(), home.pos.getY(), home.pos.getZ());
                    } else {
                        player.sendSystemMessage(Component.literal("Invalid dimension: " + home.dimension));
                    }
                }
            } else {
                player.sendSystemMessage(Component.literal("Home location not set."));
            }

            player.sendSystemMessage(Component.literal("Home command invoked"));
        }
        return Command.SINGLE_SUCCESS;
    }

}
