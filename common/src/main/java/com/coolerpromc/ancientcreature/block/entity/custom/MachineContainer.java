package com.coolerpromc.ancientcreature.block.entity.custom;

import net.minecraft.world.SimpleContainer;

public class MachineContainer extends SimpleContainer {
    private final Runnable onChanged;

    public MachineContainer(int size, Runnable onChanged) {
        super(size);
        this.onChanged = onChanged;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.onChanged.run();
    }
}
