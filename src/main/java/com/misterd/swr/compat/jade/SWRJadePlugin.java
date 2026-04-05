package com.misterd.swr.compat.jade;

import com.misterd.swr.block.custom.TimerBlock;
import com.misterd.swr.block.custom.WirelessReceiverBlock;
import com.misterd.swr.block.custom.WirelessTransmitterBlock;
import com.misterd.swr.blockentity.custom.TimerBlockEntity;
import com.misterd.swr.blockentity.custom.WirelessReceiverBlockEntity;
import com.misterd.swr.blockentity.custom.WirelessTransmitterBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SWRJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(WirelessTransmitterProvider.INSTANCE, WirelessTransmitterBlockEntity.class);
        registration.registerBlockDataProvider(WirelessReceiverProvider.INSTANCE, WirelessReceiverBlockEntity.class);
        registration.registerBlockDataProvider(TimerProvider.INSTANCE, TimerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WirelessTransmitterProvider.INSTANCE, WirelessTransmitterBlock.class);
        registration.registerBlockComponent(WirelessReceiverProvider.INSTANCE, WirelessReceiverBlock.class);
        registration.registerBlockComponent(TimerProvider.INSTANCE, TimerBlock.class);
    }
}