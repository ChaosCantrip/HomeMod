package com.chaoscantrip.homemod;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod("homemod")
public class HomeMod {

    public HomeMod() {
        MinecraftForge.EVENT_BUS.register(ModEventHandler.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            ServerLevel level = (ServerLevel) player.getServer().overworld();
            HomeSavedData homeData = HomeSavedData.get(level);

            ResourceKey<Level> dimension = player.level().dimension();
            BlockPos pos = player.blockPosition();
            Location deathLocation = new Location(pos, dimension.location().toString());

            homeData.addBack(player.getUUID(), deathLocation);
        }
    }
}

