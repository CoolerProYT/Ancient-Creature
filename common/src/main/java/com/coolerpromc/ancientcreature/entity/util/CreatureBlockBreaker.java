package com.coolerpromc.ancientcreature.entity.util;

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

    public static void destroyChargeObstacles(ServerLevel level, Mob creature, double extraReach) {
        if (!level.getGameRules().get(GameRules.MOB_GRIEFING)) {
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
            if (state.is(ModBlockTags.CREATURE_DESTROYABLE)
                    && state.getDestroySpeed(level, pos) >= 0.0F
                    && level.getBlockEntity(pos) == null) {
                level.destroyBlock(pos, true, creature);
            }
        }
    }
}
