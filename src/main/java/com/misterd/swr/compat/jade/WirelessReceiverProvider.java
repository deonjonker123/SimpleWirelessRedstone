package com.misterd.swr.compat.jade;

import com.misterd.swr.blockentity.custom.WirelessReceiverBlockEntity;
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

public enum WirelessReceiverProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath("swr", "wireless_receiver_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        int channel = data.getInt("channel");

        if (channel == -1) {
            tooltip.add(Component.translatable("jade.swr.no_channel").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("jade.swr.channel", channel).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof WirelessReceiverBlockEntity receiver) {
            data.putInt("channel", receiver.getChannel());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}