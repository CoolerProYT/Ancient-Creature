package com.coolerpromc.ancientcreature.block.entity.custom;

import net.minecraft.world.SimpleContainer;

/** A machine slot group that marks its block entity changed whenever a menu, hopper or the machine edits it. */
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
