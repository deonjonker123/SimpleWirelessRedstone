package com.misterd.swr.item;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.block.SWRBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SWRCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SimpleWirelessRedstone.MODID);

    public static final Supplier<CreativeModeTab> SWR = CREATIVE_MODE_TAB.register("swr_creativetab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(SWRBlocks.WIRELESS_TRANSMITTER.get()))
                    .title(Component.translatable("creativetab.swr"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(SWRBlocks.WIRELESS_TRANSMITTER);
                        output.accept(SWRBlocks.WIRELESS_RECEIVER);
                        output.accept(SWRBlocks.TIMER);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
