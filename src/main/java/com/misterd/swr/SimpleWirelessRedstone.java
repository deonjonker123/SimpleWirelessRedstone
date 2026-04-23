package com.misterd.swr;

import com.misterd.swr.block.SWRBlocks;
import com.misterd.swr.blockentity.SWRBlockEntities;
import com.misterd.swr.gui.SWRMenuTypes;
import com.misterd.swr.gui.custom.TimerScreen;
import com.misterd.swr.gui.custom.WirelessReceiverScreen;
import com.misterd.swr.gui.custom.WirelessTransmitterScreen;
import com.misterd.swr.item.SWRCreativeTab;
import com.misterd.swr.item.SWRItems;
import com.misterd.swr.network.SWRNetwork;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
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
        SWRNetwork.register(modEventBus);
        SWRBlockEntities.register(modEventBus);
        SWRMenuTypes.register(modEventBus);

        Config.register(modContainer);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {

        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(SWRMenuTypes.WIRELESS_TRANSMITTER_MENU.get(), WirelessTransmitterScreen::new);
            event.register(SWRMenuTypes.WIRELESS_RECEIVER_MENU.get(), WirelessReceiverScreen::new);
            event.register(SWRMenuTypes.TIMER_MENU.get(), TimerScreen::new);
        }
    }

}
