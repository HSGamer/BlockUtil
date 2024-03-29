package me.hsgamer.blockutil.abstraction;

import com.cryptomorin.xseries.XMaterial;
import com.lewdev.probabilitylib.ProbabilityCollection;
import me.hsgamer.hscore.bukkit.block.BukkitBlockAdapter;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.minecraft.block.box.BlockBox;
import me.hsgamer.hscore.minecraft.block.box.Position;
import me.hsgamer.hscore.minecraft.block.iterator.BasePositionIterator;
import me.hsgamer.hscore.minecraft.block.iterator.PositionIterator;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public interface BlockHandler {
    static Pair<World, PositionIterator> iterator(Collection<Location> locations) {
        World world = locations.stream().findAny().map(Location::getWorld).orElse(null);
        PositionIterator wrappedPositionIterator = new PositionIterator() {
            Iterator<Position> positionIterator = locations.stream().map(BukkitBlockAdapter::adapt).iterator();

            @Override
            public void reset() {
                positionIterator = locations.stream().map(BukkitBlockAdapter::adapt).iterator();
            }

            @Override
            public boolean hasNext() {
                return positionIterator.hasNext();
            }

            @Override
            public Position next() {
                return positionIterator.next();
            }
        };
        return Pair.of(world, wrappedPositionIterator);
    }

    static PositionIterator iterator(BlockBox blockBox) {
        return new BasePositionIterator(blockBox) {
            @Override
            public Position initial() {
                return new Position(blockBox.minX, blockBox.minY, blockBox.minZ);
            }

            @Override
            public Position getContinue(Position current) throws NoSuchElementException {
                if (current.x < blockBox.maxX) {
                    return new Position(current.x + 1, current.y, current.z);
                } else if (current.y < blockBox.maxY) {
                    return new Position(blockBox.minX, current.y + 1, current.z);
                } else if (current.z < blockBox.maxZ) {
                    return new Position(blockBox.minX, blockBox.minY, current.z + 1);
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public boolean hasContinue(Position current) {
                return current.x < blockBox.maxX || current.y < blockBox.maxY || current.z < blockBox.maxZ;
            }
        };
    }

    BlockProcess setRandomBlocks(World world, PositionIterator iterator, ProbabilityCollection<XMaterial> probabilityCollection);

    BlockProcess setRandomBlocks(World world, BlockBox blockBox, ProbabilityCollection<XMaterial> probabilityCollection);

    default BlockProcess setRandomBlocks(Collection<Location> locations, ProbabilityCollection<XMaterial> probabilityCollection) {
        Pair<World, PositionIterator> pair = iterator(locations);
        return setRandomBlocks(pair.getKey(), pair.getValue(), probabilityCollection);
    }

    BlockProcess setBlocks(World world, PositionIterator iterator, XMaterial material);

    BlockProcess setBlocks(World world, BlockBox blockBox, XMaterial material);

    default BlockProcess setBlocks(Collection<Location> locations, XMaterial material) {
        Pair<World, PositionIterator> pair = iterator(locations);
        return setBlocks(pair.getKey(), pair.getValue(), material);
    }

    void setBlocksFast(World world, PositionIterator iterator, XMaterial material);

    void setBlocksFast(World world, BlockBox blockBox, XMaterial material);

    default void setBlocksFast(Collection<Location> locations, XMaterial material) {
        Pair<World, PositionIterator> pair = iterator(locations);
        setBlocksFast(pair.getKey(), pair.getValue(), material);
    }

    default BlockProcess clearBlocks(World world, PositionIterator iterator) {
        return setBlocks(world, iterator, XMaterial.AIR);
    }

    default BlockProcess clearBlocks(World world, BlockBox blockBox) {
        return setBlocks(world, blockBox, XMaterial.AIR);
    }

    default BlockProcess clearBlocks(Collection<Location> locations) {
        Pair<World, PositionIterator> pair = iterator(locations);
        return clearBlocks(pair.getKey(), pair.getValue());
    }

    default void clearBlockFast(World world, PositionIterator iterator) {
        setBlocksFast(world, iterator, XMaterial.AIR);
    }

    default void clearBlocksFast(World world, BlockBox blockBox) {
        setBlocksFast(world, blockBox, XMaterial.AIR);
    }

    default void clearBlocksFast(Collection<Location> locations) {
        Pair<World, PositionIterator> pair = iterator(locations);
        clearBlockFast(pair.getKey(), pair.getValue());
    }

    BlockProcess setBlocks(World world, Map<XMaterial, Collection<Position>> blockMap);

    void setBlocksFast(World world, Map<XMaterial, Collection<Position>> blockMap);
}
