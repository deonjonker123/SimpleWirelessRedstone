package com.misterd.swr.compat.jade;

import com.misterd.swr.blockentity.custom.WirelessTransmitterBlockEntity;
import com.misterd.swr.network.ChannelManager;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum WirelessTransmitterProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath("swr", "wireless_transmitter_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        int channel = data.getInt("channel");
        int receivers = data.getInt("receivers");

        if (channel == -1) {
            tooltip.add(Component.translatable("jade.swr.no_channel").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("jade.swr.channel", channel).withStyle(ChatFormatting.YELLOW));
            tooltip.add(Component.translatable("jade.swr.connected_receivers", receivers).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof WirelessTransmitterBlockEntity transmitter
                && accessor.getLevel() instanceof ServerLevel serverLevel) {
            int channel = transmitter.getChannel();
            data.putInt("channel", channel);

            int receiverCount = 0;
            if (channel != -1 && transmitter.getOwnerUUID() != null) {
                ChannelManager mgr = ChannelManager.get(serverLevel);
                var byOwner = mgr.getReceivers().get(transmitter.getOwnerUUID());
                if (byOwner != null) {
                    var positions = byOwner.get(channel);
                    if (positions != null) receiverCount = positions.size();
                }
            }
            data.putInt("receivers", receiverCount);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}