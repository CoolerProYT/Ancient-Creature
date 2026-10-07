package com.coolerpromc.ancientcreature.entity.util;

import com.coolerpromc.ancientcreature.config.ModCommonConfig;
import com.coolerpromc.ancientcreature.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class CreatureBlockBreaker {
    private CreatureBlockBreaker() {
    }

    /**
     * Smashes {@code #creature_destroyable} blocks ahead of a charging creature, and also
     * {@code #enclosure_breakable} ones when the creature is large ({@link #canBreakEnclosures}).
     */
    public static void destroyChargeObstacles(ServerLevel level, Mob creature, double extraReach) {
        destroyAhead(level, creature, extraReach, canBreakEnclosures(creature));
    }

    /** Large creatures (at least {@value #ENCLOSURE_BREAKER_WIDTH} blocks wide) can force weak barriers. */
    public static final float ENCLOSURE_BREAKER_WIDTH = 1.2F;

    public static boolean canBreakEnclosures(Mob creature) {
        return creature.getBbWidth() >= ENCLOSURE_BREAKER_WIDTH && !creature.isBaby();
    }

    /** Breaks the blocks ahead of a creature: destroyable ones always, enclosure ones when asked. */
    public static void destroyAhead(ServerLevel level, Mob creature, double extraReach, boolean enclosures) {
        if (!level.getGameRules().get(GameRules.MOB_GRIEFING) || !ModCommonConfig.CONFIG.creaturesBreakBlocks.get()) {
            return;
        }

        Vec3 horizontalMotion = creature.getDeltaMovement().multiply(1.0, 0.0, 1.0);
        Vec3 direction;
        if (horizontalMotion.lengthSqr() > 1.0E-4) {
            direction = horizontalMotion.normalize();
        } else {
            Vec3 look = creature.getLookAngle();
            direction = new Vec3(look.x, 0.0, look.z).normalize();
        }

        double forwardDistance = creature.getBbWidth() * 0.5 + extraReach;
        AABB movedBox = creature.getBoundingBox().move(direction.scale(forwardDistance));
        AABB breakingBox = new AABB(
                movedBox.minX - 0.25,
                creature.getBoundingBox().minY + 0.05,
                movedBox.minZ - 0.25,
                movedBox.maxX + 0.25,
                movedBox.maxY + 0.1,
                movedBox.maxZ + 0.25
        );

        for (BlockPos pos : BlockPos.betweenClosed(
                Mth.floor(breakingBox.minX), Mth.floor(breakingBox.minY), Mth.floor(breakingBox.minZ),
                Mth.floor(breakingBox.maxX), Mth.floor(breakingBox.maxY), Mth.floor(breakingBox.maxZ))) {
            BlockState state = level.getBlockState(pos);
            if ((state.is(ModBlockTags.CREATURE_DESTROYABLE) || (enclosures && state.is(ModBlockTags.ENCLOSURE_BREAKABLE)))
                    && state.getDestroySpeed(level, pos) >= 0.0F
                    && level.getBlockEntity(pos) == null) {
                level.destroyBlock(pos, true, creature);
            }
        }
    }
}
