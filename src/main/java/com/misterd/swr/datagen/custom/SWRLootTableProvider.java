package com.misterd.swr.datagen.custom;

import com.misterd.swr.block.SWRBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class SWRLootTableProvider extends BlockLootSubProvider {
    public SWRLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(SWRBlocks.WIRELESS_TRANSMITTER.get());
        dropSelf(SWRBlocks.WIRELESS_RECEIVER.get());
        dropSelf(SWRBlocks.TIMER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return SWRBlocks.BLOCKS.getEntries()
                .stream()
                .map(Holder::value)
                .toList();
    }
}
