package com.misterd.swr.block.custom;

import com.misterd.swr.blockentity.custom.WirelessTransmitterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WirelessTransmitterBlock extends BaseEntityBlock {

    public static final MapCodec<WirelessTransmitterBlock> CODEC = simpleCodec(WirelessTransmitterBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED  = BlockStateProperties.POWERED;

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(1,   11, 2,   15,   14, 14  ),
            Block.box(4.5, 9,  4.5, 11.5, 11, 11.5),
            Block.box(4.5, 2,  4.5, 11.5, 4,  11.5),
            Block.box(5,   4,  5,   11,   9,  11  ),
            Block.box(3,   0,  3,   13,   2,  13  )
    );

    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(1,   11, 2,   15,   14, 14  ),
            Block.box(4.5, 9,  4.5, 11.5, 11, 11.5),
            Block.box(4.5, 2,  4.5, 11.5, 4,  11.5),
            Block.box(5,   4,  5,   11,   9,  11  ),
            Block.box(3,   0,  3,   13,   2,  13  )
    );

    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(2,   11, 1,   14,   14, 15  ),
            Block.box(4.5, 9,  4.5, 11.5, 11, 11.5),
            Block.box(4.5, 2,  4.5, 11.5, 4,  11.5),
            Block.box(5,   4,  5,   11,   9,  11  ),
            Block.box(3,   0,  3,   13,   2,  13  )
    );

    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(2,   11, 1,   14,   14, 15  ),
            Block.box(4.5, 9,  4.5, 11.5, 11, 11.5),
            Block.box(4.5, 2,  4.5, 11.5, 4,  11.5),
            Block.box(5,   4,  5,   11,   9,  11  ),
            Block.box(3,   0,  3,   13,   2,  13  )
    );

    public WirelessTransmitterBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    public MapCodec<WirelessTransmitterBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(POWERED, false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case EAST  -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST  -> SHAPE_WEST;
            default    -> SHAPE_NORTH;
        };
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player player
                && level.getBlockEntity(pos) instanceof WirelessTransmitterBlockEntity be) {
            be.setOwnerUUID(player.getUUID());
            be.registerWithChannelManager();
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof WirelessTransmitterBlockEntity be) {
                // Power off all receivers before unregistering
                be.onPoweredChanged(false);
                be.unregisterFromChannelManager();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        boolean powered = isReceivingRedstone(level, pos);
        if (powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof WirelessTransmitterBlockEntity be) {
                be.onPoweredChanged(powered);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos,
                        BlockState oldState, boolean isMoving) {
        if (level.isClientSide) return;
        boolean powered = isReceivingRedstone(level, pos);
        if (powered) {
            level.setBlock(pos, state.setValue(POWERED, true), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof WirelessTransmitterBlockEntity be) {
                be.onPoweredChanged(true);
            }
        }
    }

    private boolean isReceivingRedstone(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getSignal(pos.relative(dir), dir) > 0) return true;
        }
        return false;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof WirelessTransmitterBlockEntity transmitter) {
                player.openMenu(transmitter, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WirelessTransmitterBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}