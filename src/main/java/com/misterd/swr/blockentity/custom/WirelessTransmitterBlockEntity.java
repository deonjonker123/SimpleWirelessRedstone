package com.misterd.swr.blockentity.custom;

import com.misterd.swr.blockentity.SWRBlockEntities;
import com.misterd.swr.gui.custom.WirelessTransmitterMenu;
import com.misterd.swr.network.ChannelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class WirelessTransmitterBlockEntity extends BlockEntity implements MenuProvider {

    private static final String TAG_CHANNEL = "channel";
    private static final String TAG_OWNER = "owner";
    private static final int NO_CHANNEL = -1;

    private int channel = NO_CHANNEL;
    private UUID ownerUUID = null;

    public WirelessTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(SWRBlockEntities.WIRELESS_TRANSMITTER_BE.get(), pos, state);
    }

    public UUID getOwnerUUID() { return ownerUUID; }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
        setChanged();
    }

    public int getChannel() { return channel; }

    public void setChannel(int newChannel) {
        if (level instanceof ServerLevel serverLevel && ownerUUID != null) {
            if (channel != NO_CHANNEL) {
                ChannelManager.get(serverLevel).unregisterTransmitter(ownerUUID, channel, worldPosition);
            }
            ChannelManager.get(serverLevel).registerTransmitter(ownerUUID, newChannel, worldPosition);
        }
        this.channel = newChannel;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void clearChannel() {
        if (level instanceof ServerLevel serverLevel && ownerUUID != null && channel != NO_CHANNEL) {
            ChannelManager.get(serverLevel).unregisterTransmitter(ownerUUID, channel, worldPosition);
        }
        this.channel = NO_CHANNEL;
        setChanged();
    }

    public boolean hasChannel() { return channel != NO_CHANNEL; }

    public void onPoweredChanged(boolean powered) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (ownerUUID == null || channel == NO_CHANNEL) return;
        ChannelManager.get(serverLevel).onTransmitterPowered(serverLevel, worldPosition, ownerUUID, channel, powered);
    }

    public void registerWithChannelManager() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (ownerUUID == null || channel == NO_CHANNEL) return;
        ChannelManager.get(serverLevel).registerTransmitter(ownerUUID, channel, worldPosition);
    }

    public void unregisterFromChannelManager() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (ownerUUID == null || channel == NO_CHANNEL) return;
        ChannelManager.get(serverLevel).unregisterTransmitter(ownerUUID, channel, worldPosition);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_CHANNEL, channel);
        if (ownerUUID != null) tag.putUUID(TAG_OWNER, ownerUUID);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        channel = tag.contains(TAG_CHANNEL) ? tag.getInt(TAG_CHANNEL) : NO_CHANNEL;
        ownerUUID = tag.hasUUID(TAG_OWNER) ? tag.getUUID(TAG_OWNER) : null;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.swr.wireless_transmitter");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new WirelessTransmitterMenu(containerId, inventory, this);
    }
}