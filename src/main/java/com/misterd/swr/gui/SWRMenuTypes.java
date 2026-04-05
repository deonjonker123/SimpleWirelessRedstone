package com.misterd.swr.gui;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.gui.custom.TimerMenu;
import com.misterd.swr.gui.custom.WirelessReceiverMenu;
import com.misterd.swr.gui.custom.WirelessTransmitterMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SWRMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, SimpleWirelessRedstone.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<WirelessTransmitterMenu>>
            WIRELESS_TRANSMITTER_MENU = MENU_TYPES.register("wireless_transmitter_menu",
            () -> IMenuTypeExtension.create(WirelessTransmitterMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<WirelessReceiverMenu>>
            WIRELESS_RECEIVER_MENU = MENU_TYPES.register("wireless_receiver_menu",
            () -> IMenuTypeExtension.create(WirelessReceiverMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TimerMenu>>
            TIMER_MENU = MENU_TYPES.register("timer_menu",
            () -> IMenuTypeExtension.create(TimerMenu::new));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}