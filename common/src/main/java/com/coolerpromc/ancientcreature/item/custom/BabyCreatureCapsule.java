package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.data.component.ModDataComponents;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.custom.OwnedAncientCreature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class BabyCreatureCapsule extends Item {
    public BabyCreatureCapsule(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Species species = stack.get(ModDataComponents.SPECIES.get());
        if (level instanceof ServerLevel serverLevel && species != null){
            BlockPos pos = context.getClickedPos();
            Direction clickedFace = context.getClickedFace();
            BlockState blockState = level.getBlockState(pos);
            EntityType<?> entityType = species.getEntityType();

            BlockPos spawnPos;
            if (blockState.getCollisionShape(level, pos).isEmpty()) {
                spawnPos = pos;
            } else {
                spawnPos = pos.relative(clickedFace);
            }

            return spawnMob(context.getPlayer(), stack, serverLevel, spawnPos, true, !Objects.equals(pos, spawnPos) && clickedFace == Direction.UP, entityType);
        }
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult spawnMob(@Nullable LivingEntity user, ItemStack itemStack, ServerLevel level, BlockPos spawnPos, boolean tryMoveDown, boolean movedUp, EntityType<?> type) {
        if (type == null) {
            return InteractionResult.FAIL;
        } else if (!type.isAllowedInPeaceful() && level.getDifficulty() == Difficulty.PEACEFUL) {
            return InteractionResult.FAIL;
        } else {
            Entity spawned = type.create(level, entity -> {
                if (entity instanceof AgeableMob ageable) {
                    ageable.setBaby(true);
                }
            }, spawnPos, EntitySpawnReason.SPAWN_ITEM_USE, tryMoveDown, movedUp);
            if (spawned != null) {
                if (spawned instanceof OwnedAncientCreature creature && user != null) {
                    creature.setOwner(user);
                }
                level.addFreshEntity(spawned);
                itemStack.consume(1, user);
                level.gameEvent(user, GameEvent.ENTITY_PLACE, spawnPos);
            }

            return InteractionResult.SUCCESS;
        }
    }
}
