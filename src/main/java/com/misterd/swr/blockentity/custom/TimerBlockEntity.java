package com.misterd.swr.blockentity.custom;

import com.misterd.swr.block.custom.TimerBlock;
import com.misterd.swr.blockentity.SWRBlockEntities;
import com.misterd.swr.gui.custom.TimerMenu;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TimerBlockEntity extends BlockEntity implements MenuProvider {

    private static final String TAG_INTERVAL = "interval";
    private static final String TAG_COUNTER = "counter";
    private static final String TAG_RUNNING = "running";

    public static final int DEFAULT_INTERVAL = 20;
    public static final int MIN_INTERVAL = 2;
    public static final int MAX_INTERVAL = 10000;

    private int interval = DEFAULT_INTERVAL;
    private int counter = 0;
    private boolean running = true;

    public TimerBlockEntity(BlockPos pos, BlockState state) {
        super(SWRBlockEntities.TIMER_BE.get(), pos, state);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = Math.clamp(interval, MIN_INTERVAL, MAX_INTERVAL);
        this.counter = 0;
        setChanged();
        if (level != null && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        if (!running && level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.getValue(TimerBlock.POWERED))
                level.setBlock(worldPosition, state.setValue(TimerBlock.POWERED, false), Block.UPDATE_ALL);
        }
        setChanged();
        if (level != null && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TimerBlockEntity be) {
        if (level.isClientSide || !be.running) return;

        be.counter++;
        be.setChanged();

        if (be.counter >= be.interval) {
            be.counter = 0;
            level.setBlock(pos, state.setValue(TimerBlock.POWERED, true), Block.UPDATE_ALL);
            level.updateNeighborsAt(pos, state.getBlock());
            ((ServerLevel) level).scheduleTick(pos, state.getBlock(), 2);
        }
    }

    public void scheduledTick(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(TimerBlock.POWERED, false), Block.UPDATE_ALL);
        level.updateNeighborsAt(pos, state.getBlock());
        counter = 0;
        setChanged();
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
        tag.putInt(TAG_INTERVAL, interval);
        tag.putInt(TAG_COUNTER,  counter);
        tag.putBoolean(TAG_RUNNING, running);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        interval = tag.contains(TAG_INTERVAL) ? Math.clamp(tag.getInt(TAG_INTERVAL), MIN_INTERVAL, MAX_INTERVAL) : DEFAULT_INTERVAL;
        counter  = tag.contains(TAG_COUNTER)  ? tag.getInt(TAG_COUNTER)  : 0;
        running  = !tag.contains(TAG_RUNNING) || tag.getBoolean(TAG_RUNNING);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.swr.timer");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TimerMenu(containerId, inventory, this);
    }
}