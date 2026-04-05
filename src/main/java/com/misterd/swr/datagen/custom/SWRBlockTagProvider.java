package com.misterd.swr.datagen.custom;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.block.SWRBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SWRBlockTagProvider extends BlockTagsProvider {
    public SWRBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SimpleWirelessRedstone.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(SWRBlocks.WIRELESS_TRANSMITTER.get())
                .add(SWRBlocks.WIRELESS_RECEIVER.get())
                .add(SWRBlocks.TIMER.get());
    }
}
