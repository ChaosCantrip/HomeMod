package com.chaoscantrip.homemod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
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

            ResourceKey<Level> dimension = player.level().dimension();
            BlockPos pos = player.blockPosition();
            Location currentLocation = new Location(pos, dimension.location().toString());

            homeData.addBack(player.getUUID(), currentLocation);

            if (home != null) {
                ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(home.dimension));
                if (player.level().dimension().equals(dimensionKey)) {
                    player.teleportTo(home.pos.getX(), home.pos.getY(), home.pos.getZ());
                    player.sendSystemMessage(Component.literal("Whoosh!"));
                } else {
                    ServerLevel targetLevel = player.getServer().getLevel(dimensionKey);
                    if (targetLevel != null) {
                        player.changeDimension(targetLevel);
                        player.teleportTo(home.pos.getX(), home.pos.getY(), home.pos.getZ());
                        player.sendSystemMessage(Component.literal("Whoosh!"));
                    } else {
                        player.sendSystemMessage(Component.literal("Invalid dimension: " + home.dimension));
                    }
                }
            } else {
                BlockPos spawnPos = level.getSharedSpawnPos();
                player.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                player.sendSystemMessage(Component.literal("No home set. Teleported to world spawn."));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

}
