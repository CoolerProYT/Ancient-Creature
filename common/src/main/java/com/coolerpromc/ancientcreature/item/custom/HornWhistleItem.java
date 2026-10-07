package com.coolerpromc.ancientcreature.item.custom;

import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.entity.custom.CreatureCommand;
import com.coolerpromc.ancientcreature.entity.goal.CommandGoals;
import com.coolerpromc.ancientcreature.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;

/**
 * Gives orders to the creatures you raised.
 *
 * <ul>
 *   <li>Use on one of your creatures: cycle it between Roam, Follow and Stay.</li>
 *   <li>Use in the air: every one of your creatures within {@value #CALL_RADIUS} blocks that is not
 *       staying comes to you and follows.</li>
 *   <li>Sneak and use in the air: every one of your creatures within {@value #CALL_RADIUS} blocks stays.</li>
 * </ul>
 */
public class HornWhistleItem extends Item {
    public static final int CALL_RADIUS = 48;
    private static final int COOLDOWN = 30;

    public HornWhistleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof AncientCreatureEntity creature)) {
            return InteractionResult.PASS;
        }
        if (player.level() instanceof ServerLevel level) {
            if (!creature.isOwnedBy(player)) {
                player.sendOverlayMessage(Component.translatable("message.ancientcreature.whistle.not_yours", creature.getName()));
                return InteractionResult.FAIL;
            }
            CreatureCommand next = creature.getCommand().next();
            creature.setCommand(next);
            player.sendOverlayMessage(Component.translatable("message.ancientcreature.whistle.set", creature.getName(), next.displayName()));
            this.blow(level, player, 1.2F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel server) {
            boolean stay = player.isSecondaryUseActive();
            List<AncientCreatureEntity> mine = server.getEntitiesOfClass(AncientCreatureEntity.class,
                new AABB(player.blockPosition()).inflate(CALL_RADIUS), creature -> creature.isOwnedBy(player) && creature.isAlive());
            int count = 0;
            for (AncientCreatureEntity creature : mine) {
                if (stay) {
                    creature.setCommand(CreatureCommand.STAY);
                    count++;
                } else if (creature.getCommand() != CreatureCommand.STAY && !creature.isVehicle()) {
                    creature.setCommand(CreatureCommand.FOLLOW);
                    if (creature.distanceToSqr(player) > 24 * 24 && !creature.speciesCategory().isAquatic()) {
                        CommandGoals.teleportNear(creature, player.blockPosition());
                    }
                    count++;
                }
            }
            player.sendOverlayMessage(count == 0
                ? Component.translatable("message.ancientcreature.whistle.nobody")
                : Component.translatable(stay ? "message.ancientcreature.whistle.all_stay" : "message.ancientcreature.whistle.all_follow", count));
            this.blow(server, player, stay ? 0.8F : 1.0F);
            player.getCooldowns().addCooldown(player.getItemInHand(hand), COOLDOWN);
        }
        return InteractionResult.SUCCESS;
    }

    private void blow(ServerLevel level, Player player, float pitch) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.HORN_WHISTLE_BLOW.get(), SoundSource.PLAYERS, 1.2F, pitch);
        level.gameEvent(player, GameEvent.INSTRUMENT_PLAY, player.position());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ancientcreature.whistle.creature").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltip.ancientcreature.whistle.call", CALL_RADIUS).withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltip.ancientcreature.whistle.stay").withStyle(ChatFormatting.GRAY));
    }
}
