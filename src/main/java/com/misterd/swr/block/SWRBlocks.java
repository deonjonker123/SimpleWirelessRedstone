package com.misterd.swr.block;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.block.custom.TimerBlock;
import com.misterd.swr.block.custom.WirelessReceiverBlock;
import com.misterd.swr.block.custom.WirelessTransmitterBlock;
import com.misterd.swr.item.SWRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class SWRBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SimpleWirelessRedstone.MODID);

    public static final DeferredBlock<Block> WIRELESS_RECEIVER = registerBlock("wireless_receiver",
            () -> new WirelessReceiverBlock(BlockBehaviour.Properties.of()
                    .strength(1F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.swr.wireless_receiver.subtitle").withStyle(ChatFormatting.GOLD));
                }
            });

    public static final DeferredBlock<Block> WIRELESS_TRANSMITTER = registerBlock("wireless_transmitter",
            () -> new WirelessTransmitterBlock(BlockBehaviour.Properties.of()
                    .strength(1F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.swr.wireless_transmitter.subtitle").withStyle(ChatFormatting.GOLD));
                }
            });

    public static final DeferredBlock<Block> TIMER = registerBlock("timer",
            () -> new TimerBlock(BlockBehaviour.Properties.of()
                    .strength(1F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.swr.timer.subtitle").withStyle(ChatFormatting.GOLD));
                }
            });

    private static <T extends Block> DeferredBlock<T> registerBlock (String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem (String name, DeferredBlock<T> block) {
        SWRItems.ITEMS.register (name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register (IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
