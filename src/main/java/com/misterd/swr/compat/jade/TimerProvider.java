package com.misterd.swr.compat.jade;

import com.misterd.swr.blockentity.custom.TimerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum TimerProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath("swr", "timer_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        int interval = data.getInt("interval");
        boolean running = data.getBoolean("running");

        tooltip.add(Component.translatable("jade.swr.timer.interval", interval).withStyle(ChatFormatting.YELLOW));
        tooltip.add(running
                ? Component.translatable("jade.swr.timer.running").withStyle(ChatFormatting.GREEN)
                : Component.translatable("jade.swr.timer.stopped").withStyle(ChatFormatting.RED));
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof TimerBlockEntity timer) {
            data.putInt("interval", timer.getInterval());
            data.putBoolean("running", timer.isRunning());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}