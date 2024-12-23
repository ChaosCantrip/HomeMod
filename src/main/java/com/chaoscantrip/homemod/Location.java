package com.chaoscantrip.homemod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public class Location {
    public final BlockPos pos;
    public final String dimension;

    public Location(BlockPos pos, String dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    public static final Codec<Location> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockPos.CODEC.fieldOf("pos").forGetter(location -> location.pos),
        Codec.STRING.fieldOf("dimension").forGetter(location -> location.dimension)
    ).apply(instance, Location::new));
}
