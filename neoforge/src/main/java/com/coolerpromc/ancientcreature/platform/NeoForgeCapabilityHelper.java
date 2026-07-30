package com.coolerpromc.ancientcreature.platform;

import com.coolerpromc.ancientcreature.platform.services.ICapabilityHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NeoForgeCapabilityHelper implements ICapabilityHelper {
    private final List<Entry<? extends BlockEntity>> entries = new ArrayList<>();

    @Override
    public <T extends BlockEntity> void registerBlockEntityItemStorage(Supplier<BlockEntityType<T>> type, BiFunction<T, @Nullable Direction, Container> provider) {
        entries.add(new Entry<>(type, provider));
    }

    @Override
    public <T> void applyRegistrations(T event) {
        if (event instanceof RegisterCapabilitiesEvent capabilitiesEvent){
            for (Entry<? extends BlockEntity> entry : entries) {
                register(capabilitiesEvent, entry);
            }
        }
    }

    private static <T extends BlockEntity> void register(RegisterCapabilitiesEvent event, Entry<T> entry) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, entry.type().get(), (blockEntity, direction) -> VanillaContainerWrapper.of(entry.provider().apply(blockEntity, direction)));
    }

    private record Entry<T extends BlockEntity>(Supplier<BlockEntityType<T>> type, BiFunction<T, @Nullable Direction, Container> provider){

    }
}
