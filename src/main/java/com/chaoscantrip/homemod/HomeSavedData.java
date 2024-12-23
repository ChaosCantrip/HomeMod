package com.chaoscantrip.homemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomeSavedData extends SavedData {
    private static final String DATA_NAME = "home_data";
    private final Map<UUID, Location> homes = new HashMap<>();
    private final Map<UUID, List<Location>> backs = new HashMap<>();

    public Location getHome(UUID playerUUID) {
        return homes.get(playerUUID);
    }

    public void setHome(UUID playerUUID, Location home) {
        homes.put(playerUUID, home);
        setDirty();
    }

    public List<Location> getBacks(UUID playerUUID) {
        return backs.getOrDefault(playerUUID, List.of());
    }

    public void addBack(UUID playerUUID, Location back) {
        backs.computeIfAbsent(playerUUID, k -> new java.util.ArrayList<>()).add(0, back);
        if (backs.get(playerUUID).size() > 5) {
            backs.get(playerUUID).remove(5);
        }
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag homesTag = new CompoundTag();
        for (Map.Entry<UUID, Location> entry : homes.entrySet()) {
            homesTag.put(entry.getKey().toString(), Location.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result().orElse(new CompoundTag()));
        }
        compound.put("homes", homesTag);

        CompoundTag backsTag = new CompoundTag();
        for (Map.Entry<UUID, List<Location>> entry : backs.entrySet()) {
            ListTag listTag = new ListTag();
            for (Location loc : entry.getValue()) {
                listTag.add(Location.CODEC.encodeStart(NbtOps.INSTANCE, loc).result().orElse(new CompoundTag()));
            }
            backsTag.put(entry.getKey().toString(), listTag);
        }
        compound.put("backs", backsTag);

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

        CompoundTag backsTag = compound.getCompound("backs");
        for (String key : backsTag.getAllKeys()) {
            UUID playerUUID = UUID.fromString(key);
            ListTag listTag = backsTag.getList(key, 10);
            List<Location> backList = listTag.stream()
                    .map(tag -> Location.CODEC.parse(NbtOps.INSTANCE, (CompoundTag) tag).result().orElse(null))
                    .collect(Collectors.toList());
            data.backs.put(playerUUID, backList);
        }

        return data;
    }

    public static HomeSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(HomeSavedData::load, HomeSavedData::new, DATA_NAME);
    }
}