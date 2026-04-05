package com.misterd.swr;

import com.misterd.swr.block.SWRBlocks;
import com.misterd.swr.item.SWRCreativeTab;
import com.misterd.swr.item.SWRItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(SimpleWirelessRedstone.MODID)
public class SimpleWirelessRedstone {
    public static final String MODID = "swr";

    public SimpleWirelessRedstone(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        SWRBlocks.register(modEventBus);
        SWRItems.register(modEventBus);
        SWRCreativeTab.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
