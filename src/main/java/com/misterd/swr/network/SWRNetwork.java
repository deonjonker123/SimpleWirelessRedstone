package com.misterd.swr.network;

import com.misterd.swr.SimpleWirelessRedstone;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SWRNetwork {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(SWRNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(SimpleWirelessRedstone.MODID);

        registrar.playToServer(
                SetChannelPacket.TYPE,
                SetChannelPacket.STREAM_CODEC,
                SetChannelPacket::handle);

        registrar.playToServer(
                SetIntervalPacket.TYPE,
                SetIntervalPacket.STREAM_CODEC,
                SetIntervalPacket::handle);

        registrar.playToServer(
                SetRunningPacket.TYPE,
                SetRunningPacket.STREAM_CODEC,
                SetRunningPacket::handle);
    }
}