package com.misterd.swr.network;

import com.misterd.swr.Config;
import com.misterd.swr.block.custom.WirelessReceiverBlock;
import com.misterd.swr.blockentity.custom.WirelessReceiverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChannelManager extends SavedData {

    private static final String DATA_NAME = "swr_channel_manager";

    private final Map<UUID, Map<Integer, Set<BlockPos>>> transmitters = new HashMap<>();

    private final Map<UUID, Map<Integer, Set<BlockPos>>> receivers = new HashMap<>();

    public Map<UUID, Map<Integer, Set<BlockPos>>> getReceivers() {
        return receivers;
    }

    public static ChannelManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ChannelManager::new, ChannelManager::load),
                DATA_NAME);
    }

    public void registerTransmitter(UUID owner, int channel, BlockPos pos) {
        transmitters
                .computeIfAbsent(owner, k -> new HashMap<>())
                .computeIfAbsent(channel, k -> new HashSet<>())
                .add(pos);
        setDirty();
    }

    public void unregisterTransmitter(UUID owner, int channel, BlockPos pos) {
        Map<Integer, Set<BlockPos>> byChannel = transmitters.get(owner);
        if (byChannel == null) return;
        Set<BlockPos> positions = byChannel.get(channel);
        if (positions == null) return;
        positions.remove(pos);
        if (positions.isEmpty()) byChannel.remove(channel);
        if (byChannel.isEmpty()) transmitters.remove(owner);
        setDirty();
    }

    public void registerReceiver(UUID owner, int channel, BlockPos pos) {
        receivers
                .computeIfAbsent(owner, k -> new HashMap<>())
                .computeIfAbsent(channel, k -> new HashSet<>())
                .add(pos);
        setDirty();
    }

    public void unregisterReceiver(UUID owner, int channel, BlockPos pos) {
        Map<Integer, Set<BlockPos>> byChannel = receivers.get(owner);
        if (byChannel == null) return;
        Set<BlockPos> positions = byChannel.get(channel);
        if (positions == null) return;
        positions.remove(pos);
        if (positions.isEmpty()) byChannel.remove(channel);
        if (byChannel.isEmpty()) receivers.remove(owner);
        setDirty();
    }

    public void onTransmitterPowered(ServerLevel level, BlockPos transmitterPos, UUID owner, int channel, boolean powered) {
        if (channel < 0) return;

        Map<Integer, Set<BlockPos>> byChannel = receivers.get(owner);
        if (byChannel == null) return;
        Set<BlockPos> targets = byChannel.get(channel);
        if (targets == null || targets.isEmpty()) return;

        int range = Config.getTransmissionRange();
        double rangeSq = (double) range * range;

        for (BlockPos receiverPos : Set.copyOf(targets)) {
            if (transmitterPos.distSqr(receiverPos) > rangeSq) continue;

            BlockState state = level.getBlockState(receiverPos);
            if (!(state.getBlock() instanceof WirelessReceiverBlock)) {
                targets.remove(receiverPos);
                setDirty();
                continue;
            }

            if (state.getValue(WirelessReceiverBlock.POWERED) != powered) {
                level.setBlock(receiverPos, state.setValue(WirelessReceiverBlock.POWERED, powered), 3);
            }

            if (level.getBlockEntity(receiverPos) instanceof WirelessReceiverBlockEntity be) {
                level.sendBlockUpdated(receiverPos, state, state, 3);
            }
        }
    }

    public void onReceiverChannelChanged(ServerLevel level, BlockPos receiverPos, UUID owner, int channel) {
        if (channel < 0) return;

        Map<Integer, Set<BlockPos>> byChannel = transmitters.get(owner);
        if (byChannel == null) return;
        Set<BlockPos> sources = byChannel.get(channel);
        if (sources == null || sources.isEmpty()) return;

        int range = Config.getTransmissionRange();
        double rangeSq = (double) range * range;

        for (BlockPos transmitterPos : sources) {
            if (transmitterPos.distSqr(receiverPos) > rangeSq) continue;
            BlockState txState = level.getBlockState(transmitterPos);
            if (!txState.hasProperty(com.misterd.swr.block.custom.WirelessTransmitterBlock.POWERED)) continue;
            if (txState.getValue(com.misterd.swr.block.custom.WirelessTransmitterBlock.POWERED)) {
                BlockState rxState = level.getBlockState(receiverPos);
                if (rxState.getBlock() instanceof WirelessReceiverBlock) {
                    level.setBlock(receiverPos, rxState.setValue(WirelessReceiverBlock.POWERED, true), 3);
                }
                return;
            }
        }
    }

    public static ChannelManager load(CompoundTag tag, HolderLookup.Provider registries) {
        ChannelManager manager = new ChannelManager();
        manager.loadEntries(tag.getList("transmitters", Tag.TAG_COMPOUND), manager.transmitters);
        manager.loadEntries(tag.getList("receivers",    Tag.TAG_COMPOUND), manager.receivers);
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("transmitters", saveEntries(transmitters));
        tag.put("receivers",    saveEntries(receivers));
        return tag;
    }

    private void loadEntries(ListTag list, Map<UUID, Map<Integer, Set<BlockPos>>> target) {
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            UUID owner = entry.getUUID("owner");
            int channel = entry.getInt("channel");
            BlockPos pos = BlockPos.of(entry.getLong("pos"));
            target
                    .computeIfAbsent(owner, k -> new HashMap<>())
                    .computeIfAbsent(channel, k -> new HashSet<>())
                    .add(pos);
        }
    }

    private ListTag saveEntries(Map<UUID, Map<Integer, Set<BlockPos>>> source) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Map<Integer, Set<BlockPos>>> byOwner : source.entrySet()) {
            for (Map.Entry<Integer, Set<BlockPos>> byChannel : byOwner.getValue().entrySet()) {
                for (BlockPos pos : byChannel.getValue()) {
                    CompoundTag entry = new CompoundTag();
                    entry.putUUID("owner", byOwner.getKey());
                    entry.putInt("channel", byChannel.getKey());
                    entry.putLong("pos", pos.asLong());
                    list.add(entry);
                }
            }
        }
        return list;
    }
}