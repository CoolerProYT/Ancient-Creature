package com.coolerpromc.ancientcreature.entity.goal;

import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.entity.custom.CreatureCommand;
import com.coolerpromc.ancientcreature.entity.util.CreatureBlockBreaker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * A distressed large creature tries to break out: it picks a direction and shoves straight along it,
 * ignoring the pathfinder (which would never lead it into a fence), and smashes weak barriers in its way.
 * Reinforced and electric fences are not in {@code #enclosure_breakable}, so a proper enclosure holds.
 */
public class RampageGoal extends Goal {
    private static final int DURATION = 80;

    private final AncientCreatureEntity creature;
    private Vec3 heading = Vec3.ZERO;
    private int ticks;
    private int cooldown;

    public RampageGoal(AncientCreatureEntity creature) {
        this.creature = creature;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (--this.cooldown > 0) {
            return false;
        }
        this.cooldown = this.adjustedTickDelay(200 + this.creature.getRandom().nextInt(400));
        return this.creature.isDistressed() && CreatureBlockBreaker.canBreakEnclosures(this.creature)
            && this.creature.getCommand() != CreatureCommand.STAY && !this.creature.isVehicle() && this.creature.onGround()
            && this.creature.getRandom().nextInt(3) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.ticks < DURATION && this.creature.isDistressed() && !this.creature.isVehicle();
    }

    @Override
    public void start() {
        this.ticks = 0;
        double angle = this.creature.getRandom().nextDouble() * Math.PI * 2;
        this.heading = new Vec3(Math.cos(angle), 0, Math.sin(angle));
        this.creature.setSprinting(true);
    }

    @Override
    public void stop() {
        this.creature.setSprinting(false);
        this.creature.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.ticks++;
        Vec3 target = this.creature.position().add(this.heading.scale(4.0));
        this.creature.getMoveControl().setWantedPosition(target.x, this.creature.getY(), target.z, 1.3);
        this.creature.getLookControl().setLookAt(target.x, this.creature.getEyeY(), target.z);
        if (this.creature.level() instanceof ServerLevel level && this.ticks % 4 == 0) {
            CreatureBlockBreaker.destroyAhead(level, this.creature, 0.6, true);
        }
    }
}
