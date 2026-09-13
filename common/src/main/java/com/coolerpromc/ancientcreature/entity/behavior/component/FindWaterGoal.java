package com.coolerpromc.ancientcreature.entity.behavior.component;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

/** Replacement for vanilla's removed generic TryFindWaterGoal: sends a beached mob back towards nearby water. */
public class FindWaterGoal extends MoveToBlockGoal {
    public FindWaterGoal(PathfinderMob mob) {
        super(mob, 1.0, 20);
    }

    @Override
    public boolean canUse() {
        return !this.mob.isInWater() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.isInWater() && super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER);
    }
}
