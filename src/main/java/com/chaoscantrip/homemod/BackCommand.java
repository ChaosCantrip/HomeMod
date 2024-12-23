package com.chaoscantrip.homemod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class BackCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("back").executes(BackCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> command) {
        if (command.getSource().getEntity() instanceof Player player) {
            ServerLevel level = (ServerLevel) player.getServer().overworld();
            HomeSavedData homeData = HomeSavedData.get(level);
            List<Location> backs = homeData.getBacks(player.getUUID());

            if (!backs.isEmpty()) {
                Location back = backs.remove(0);
                homeData.setDirty();
                ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(back.dimension));
                if (player.level().dimension().equals(dimensionKey)) {
                    player.teleportTo(back.pos.getX(), back.pos.getY(), back.pos.getZ());
                } else {
                    ServerLevel targetLevel = player.getServer().getLevel(dimensionKey);
                    if (targetLevel != null) {
                        player.changeDimension(targetLevel);
                        player.teleportTo(back.pos.getX(), back.pos.getY(), back.pos.getZ());
                    } else {
                        player.sendSystemMessage(Component.literal("Invalid dimension: " + back.dimension));
                    }
                }
            } else {
                player.sendSystemMessage(Component.literal("No back locations available."));
            }

            player.sendSystemMessage(Component.literal("Back command invoked"));
        }
        return Command.SINGLE_SUCCESS;
    }
}