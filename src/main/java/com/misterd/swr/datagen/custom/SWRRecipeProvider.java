package com.misterd.swr.datagen.custom;

import com.misterd.swr.block.SWRBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class SWRRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public SWRRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SWRBlocks.WIRELESS_TRANSMITTER.get())
                .pattern("TYT")
                .pattern("ESE")
                .pattern("RSR")
                .define('S', Tags.Items.STONES)
                .define('Y', Items.REPEATER)
                .define('E', Items.ENDER_PEARL)
                .define('R', Items.REDSTONE)
                .define('T', Items.REDSTONE_TORCH)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SWRBlocks.WIRELESS_RECEIVER.get())
                .pattern(" T ")
                .pattern("TET")
                .pattern("SRS")
                .define('S', Tags.Items.STONES)
                .define('E', Items.ENDER_PEARL)
                .define('R', Items.REDSTONE)
                .define('T', Items.REDSTONE_TORCH)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SWRBlocks.TIMER.get())
                .pattern(" T ")
                .pattern("RYR")
                .pattern("SSS")
                .define('S', Tags.Items.STONES)
                .define('Y', Items.COMPARATOR)
                .define('R', Items.REDSTONE)
                .define('T', Items.REDSTONE_TORCH)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(recipeOutput);
    }
}
