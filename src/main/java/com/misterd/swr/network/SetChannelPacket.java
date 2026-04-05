package com.misterd.swr.network;

import com.misterd.swr.SimpleWirelessRedstone;
import com.misterd.swr.blockentity.custom.WirelessReceiverBlockEntity;
import com.misterd.swr.blockentity.custom.WirelessTransmitterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetChannelPacket(BlockPos pos, int channel) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetChannelPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(SimpleWirelessRedstone.MODID, "set_channel"));

    public static final StreamCodec<FriendlyByteBuf, SetChannelPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetChannelPacket::pos,
                    ByteBufCodecs.INT, SetChannelPacket::channel,
                    SetChannelPacket::new);

    @Override
    public CustomPacketPayload.Type<SetChannelPacket> type() {
        return TYPE;
    }

    public static void handle(SetChannelPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            int safeChannel = Math.max(0, packet.channel());
            if (player.distanceToSqr(packet.pos().getCenter()) > 64) return;

            BlockEntity be = player.level().getBlockEntity(packet.pos());
            if (be instanceof WirelessTransmitterBlockEntity transmitter) {
                transmitter.setChannel(safeChannel);
            } else if (be instanceof WirelessReceiverBlockEntity receiver) {
                receiver.setChannel(safeChannel);
            }
        });
    }
}