package com.chaoscantrip.homemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeSavedData extends SavedData {
    private static final String DATA_NAME = "home_data";
    private final Map<UUID, Location> homes = new HashMap<>();

    public Location getHome(UUID playerUUID) {
        return homes.get(playerUUID);
    }

    public void setHome(UUID playerUUID, Location home) {
        homes.put(playerUUID, home);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag homesTag = new CompoundTag();
        for (Map.Entry<UUID, Location> entry : homes.entrySet()) {
            homesTag.put(entry.getKey().toString(), Location.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result().orElse(new CompoundTag()));
        }
        compound.put("homes", homesTag);
        return compound;
    }

    public static HomeSavedData load(CompoundTag compound) {
        HomeSavedData data = new HomeSavedData();
        CompoundTag homesTag = compound.getCompound("homes");
        for (String key : homesTag.getAllKeys()) {
            UUID playerUUID = UUID.fromString(key);
            Location home = Location.CODEC.parse(NbtOps.INSTANCE, homesTag.getCompound(key)).result().orElse(null);
            data.homes.put(playerUUID, home);
        }
        return data;
    }

    public static HomeSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(HomeSavedData::load, HomeSavedData::new, DATA_NAME);
    }
}