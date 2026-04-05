package com.misterd.swr;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

public class Config {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec COMMON_CONFIG;

    private static ModConfigSpec.IntValue TRANSMISSION_RANGE;

    static {
        buildCommonConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    private static void buildCommonConfig() {
        COMMON_BUILDER.comment("Simple Wireless Redstone - Configuration")
                .push("transmission");

        TRANSMISSION_RANGE = COMMON_BUILDER
                .comment(
                        "Transmission range in blocks (radius from the transmitter)",
                        "A value of 128 means the signal reaches up to 128 blocks in all directions",
                        "WARNING: Very large values can impact server performance on busy servers!"
                )
                .defineInRange("transmission_range", 128, 16, 512);

        COMMON_BUILDER.pop();
    }

    public static int getTransmissionRange() {
        return TRANSMISSION_RANGE.get();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            LOGGER.info("Simple Wireless Redstone configuration loaded");
            LOGGER.info("  Transmission range: {} blocks ({} block diameter)", getTransmissionRange(), getTransmissionRange() * 2);
            if (getTransmissionRange() > 256) {
                LOGGER.warn("Transmission range ({}) is very large and may impact server performance!", getTransmissionRange());
            }
        }
    }
}