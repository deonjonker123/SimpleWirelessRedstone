package com.misterd.swr.item;

import com.misterd.swr.SimpleWirelessRedstone;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SWRItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimpleWirelessRedstone.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
