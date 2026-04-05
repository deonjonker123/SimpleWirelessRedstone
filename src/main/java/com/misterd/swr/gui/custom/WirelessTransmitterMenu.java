package com.misterd.swr.gui.custom;

import com.misterd.swr.blockentity.custom.WirelessTransmitterBlockEntity;
import com.misterd.swr.gui.SWRMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WirelessTransmitterMenu extends AbstractContainerMenu {

    private final WirelessTransmitterBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public WirelessTransmitterMenu(int containerId, Inventory inventory, WirelessTransmitterBlockEntity blockEntity) {
        super(SWRMenuTypes.WIRELESS_TRANSMITTER_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    public WirelessTransmitterMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(containerId, inventory, getBlockEntity(inventory, buf));
    }

    private static WirelessTransmitterBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity be = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof WirelessTransmitterBlockEntity transmitter) return transmitter;
        throw new IllegalStateException("Block entity is not a WirelessTransmitterBlockEntity");
    }

    public int getChannel() {
        return blockEntity.getChannel();
    }

    public void setChannel(int channel) {
        blockEntity.setChannel(channel);
    }

    public void clearChannel() {
        blockEntity.clearChannel();
    }

    public boolean hasChannel() {
        return blockEntity.hasChannel();
    }

    public BlockPos getBlockPos() {
        return blockEntity.getBlockPos();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, blockEntity.getBlockState().getBlock());
    }
}