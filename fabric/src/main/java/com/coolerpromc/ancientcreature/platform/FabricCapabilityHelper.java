package com.coolerpromc.ancientcreature.platform;

import com.coolerpromc.ancientcreature.platform.services.ICapabilityHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FabricCapabilityHelper implements ICapabilityHelper {
    private final List<Entry<? extends BlockEntity>> entries = new ArrayList<>();

    @Override
    public <T extends BlockEntity> void registerBlockEntityItemStorage(Supplier<BlockEntityType<T>> type, BiFunction<T, @Nullable Direction, Container> provider) {
        entries.add(new Entry<>(type, provider));
    }

    @Override
    public <T> void applyRegistrations(T event) {
        for (Entry<? extends BlockEntity> entry : entries) {
            register(entry);
        }
    }

    private static <T extends BlockEntity> void register(Entry<T> entry) {
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> ContainerStorage.of(entry.provider.apply(blockEntity, direction), direction), entry.type.get());
    }

    private record Entry<T extends BlockEntity>(Supplier<BlockEntityType<T>> type, BiFunction<T, @Nullable Direction, Container> provider){
    }
}
