package com.coolerpromc.ancientcreature.block.entity.custom;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class PlaceholderBlockEntity extends BlockEntity implements ICapabilityExposure{
    private double minX = 0.0;
    private double minY = 0.0;
    private double minZ = 0.0;
    private double maxX = 16.0;
    private double maxY = 16.0;
    private double maxZ = 16.0;
    private boolean shouldDrop = true;
    private BlockPos actualPos = BlockPos.ZERO;

    public PlaceholderBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.PLACEHOLDER.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putDouble("minX", minX);
        output.putDouble("minY", minY);
        output.putDouble("minZ", minZ);
        output.putDouble("maxX", maxX);
        output.putDouble("maxY", maxY);
        output.putDouble("maxZ", maxZ);
        output.putBoolean("shouldDrop", shouldDrop);
        output.store("actualPos", BlockPos.CODEC, actualPos);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.minX = input.getDoubleOr("minX", 0.0);
        this.minY = input.getDoubleOr("minY", 0.0);
        this.minZ = input.getDoubleOr("minZ", 0.0);
        this.maxX = input.getDoubleOr("maxX", 16.0);
        this.maxY = input.getDoubleOr("maxY", 16.0);
        this.maxZ = input.getDoubleOr("maxZ", 16.0);
        this.shouldDrop = input.getBooleanOr("shouldDrop", false);
        this.actualPos = input.read("actualPos", BlockPos.CODEC).orElse(BlockPos.ZERO);
    }

    public VoxelShape getShape(){
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void setMinX(double minX) {
        this.minX = minX;
        setChanged();
    }

    public void setMinY(double minY) {
        this.minY = minY;
        setChanged();
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
        setChanged();
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
        setChanged();
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
        setChanged();
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
        setChanged();
    }

    public void setShouldDrop(boolean shouldDrop) {
        this.shouldDrop = shouldDrop;
        setChanged();
    }

    public void setActualPos(BlockPos actualPos) {
        this.actualPos = actualPos;
        setChanged();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try(ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), Constants.LOG)){
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            saveAdditional(output);
            return output.buildResult();
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public BlockPos getActualPos() {
        return actualPos;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        BlockState actualState = level.getBlockState(actualPos);

        if (!actualState.isAir()) {
            level.destroyBlock(actualPos, shouldDrop);
        }
    }

    public Container getContainerBySide(@Nullable Direction direction) {
        BlockEntity actual = level.getBlockEntity(this.getActualPos());
        if (actual instanceof ICapabilityExposure exposure){
            return exposure.getContainerBySide(direction);
        }
        return null;
    }
}
