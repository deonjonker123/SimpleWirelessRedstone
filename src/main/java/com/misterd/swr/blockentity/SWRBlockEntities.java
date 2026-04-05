package com.misterd.swr.blockentity;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.block.SWRBlocks;
import com.misterd.swr.blockentity.custom.TimerBlockEntity;
import com.misterd.swr.blockentity.custom.WirelessReceiverBlockEntity;
import com.misterd.swr.blockentity.custom.WirelessTransmitterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class SWRBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SimpleWirelessRedstone.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WirelessTransmitterBlockEntity>>
            WIRELESS_TRANSMITTER_BE = BLOCK_ENTITIES.register("wireless_transmitter_be",
            () -> BlockEntityType.Builder
                    .of(WirelessTransmitterBlockEntity::new, SWRBlocks.WIRELESS_TRANSMITTER.get())
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WirelessReceiverBlockEntity>>
            WIRELESS_RECEIVER_BE = BLOCK_ENTITIES.register("wireless_receiver_be",
            () -> BlockEntityType.Builder
                    .of(WirelessReceiverBlockEntity::new, SWRBlocks.WIRELESS_RECEIVER.get())
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TimerBlockEntity>>
            TIMER_BE = BLOCK_ENTITIES.register("timer_be",
            () -> BlockEntityType.Builder
                    .of(TimerBlockEntity::new, SWRBlocks.TIMER.get())
                    .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}