package com.misterd.swr.gui.custom;

import com.misterd.swr.blockentity.custom.TimerBlockEntity;
import com.misterd.swr.gui.SWRMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TimerMenu extends AbstractContainerMenu {

    private final TimerBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public TimerMenu(int containerId, Inventory inventory, TimerBlockEntity blockEntity) {
        super(SWRMenuTypes.TIMER_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    public TimerMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(containerId, inventory, getBlockEntity(inventory, buf));
    }

    private static TimerBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity be = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof TimerBlockEntity timer) return timer;
        throw new IllegalStateException("Block entity is not a TimerBlockEntity");
    }

    public int getInterval() { return blockEntity.getInterval(); }
    public boolean isRunning() { return blockEntity.isRunning(); }
    public BlockPos getBlockPos() { return blockEntity.getBlockPos(); }

    @Override
    public ItemStack quickMoveStack(Player player, int i) { return null; }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, blockEntity.getBlockState().getBlock());
    }
}