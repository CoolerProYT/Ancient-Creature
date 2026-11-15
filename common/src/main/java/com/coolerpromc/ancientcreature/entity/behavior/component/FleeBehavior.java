package com.coolerpromc.ancientcreature.entity.behavior.component;

import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorConfig;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public record FleeBehavior(double walkSpeed, double sprintSpeed, float range, boolean fromPlayers, boolean fromPredators)
    implements CreatureBehaviorConfig {

    public static final MapCodec<FleeBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        BehaviorCodecs.SPEED.optionalFieldOf("walk_speed", 1.2).forGetter(FleeBehavior::walkSpeed),
        BehaviorCodecs.SPEED.optionalFieldOf("sprint_speed", 1.7).forGetter(FleeBehavior::sprintSpeed),
        BehaviorCodecs.DISTANCE_F.optionalFieldOf("range", 12.0F).forGetter(FleeBehavior::range),
        Codec.BOOL.optionalFieldOf("from_players", true).forGetter(FleeBehavior::fromPlayers),
        Codec.BOOL.optionalFieldOf("from_predators", true).forGetter(FleeBehavior::fromPredators)
    ).apply(i, FleeBehavior::new));

    @Override
    public CreatureBehaviorType<?> type() {
        return CreatureBehaviorRegistry.FLEE;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        return new AvoidEntityGoal<>(entity, LivingEntity.class, this.range, this.walkSpeed, this.sprintSpeed,
            other -> this.isThreat(entity, other)) {
            @Override
            public boolean canUse() {
                return entity.getTarget() == null && !entity.isSitting() && !entity.isVehicle() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return entity.getTarget() == null && !entity.isSitting() && !entity.isVehicle()
                    && this.toAvoid != null && FleeBehavior.this.isThreat(entity, this.toAvoid)
                    && super.canContinueToUse();
            }

            @Override
            public void tick() {
                super.tick();

                entity.setSprinting(this.toAvoid != null && entity.distanceToSqr(this.toAvoid) < 49.0);
            }

            @Override
            public void stop() {
                super.stop();
                entity.getNavigation().stop();
                entity.setSprinting(false);
            }
        };
    }

    private boolean isThreat(AncientCreatureEntity self, LivingEntity other) {
        if (other == self || !other.isAlive()) {
            return false;
        }
        if (other instanceof Player player) {
            return this.fromPlayers && !player.isCreative() && !player.isSpectator() && !player.isShiftKeyDown()
                && !self.isOwnedBy(player);
        }
        if (this.fromPredators && other instanceof AncientCreatureEntity creature) {
            return !creature.isBaby() && !creature.getSpecies().equals(self.getSpecies()) && isPredator(creature);
        }
        return false;
    }

    public static boolean isPredator(AncientCreatureEntity creature) {
        if (!creature.hasResolvedSpecies()) {
            return false;
        }
        for (CreatureBehaviorComponent component : creature.speciesDefinition().resolvedBehaviors()) {
            CreatureBehaviorType<?> type = component.config().type();
            if (type == CreatureBehaviorRegistry.HUNT_ANIMALS || type == CreatureBehaviorRegistry.AQUATIC_PREDATOR) {
                return true;
            }
        }
        return false;
    }
}
