package com.coolerpromc.ancientcreature.command;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.entity.ModEntities;
import com.coolerpromc.ancientcreature.entity.Species;
import com.coolerpromc.ancientcreature.entity.behavior.BehaviorProfiles;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorComponent;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorRegistry;
import com.coolerpromc.ancientcreature.entity.behavior.CreatureBehaviorType;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureAction;
import com.coolerpromc.ancientcreature.entity.custom.AncientCreatureEntity;
import com.coolerpromc.ancientcreature.species.SpeciesDefinition;
import com.coolerpromc.ancientcreature.species.SpeciesHungerProperties;
import com.coolerpromc.ancientcreature.species.SpeciesManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class AncientCreatureCommand {
    private static final SuggestionProvider<CommandSourceStack> SPECIES_SUGGESTIONS = (context, builder) -> SharedSuggestionProvider.suggestResource(SpeciesManager.SERVER.ids(), builder);

    private static final SuggestionProvider<CommandSourceStack> ACTION_SUGGESTIONS = (context, builder) ->
        SharedSuggestionProvider.suggest(Arrays.stream(AncientCreatureAction.values()).map(AncientCreatureAction::queryName), builder);

    /** {@code ticks} value meaning "leave the action set until something clears it". */
    private static final int HOLD = 0;

    /** Replay period for {@code loop} when none is given; comfortably longer than any shipped clip. */
    private static final int DEFAULT_LOOP_PERIOD = 60;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Constants.MODID).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));

        root.then(Commands.literal("species")
            .then(Commands.literal("list").executes(AncientCreatureCommand::list))
            .then(Commands.literal("validate").executes(AncientCreatureCommand::validate))
            .then(Commands.literal("reload").executes(AncientCreatureCommand::reload))
            .then(Commands.literal("get")
                .then(Commands.argument("id", IdentifierArgument.id())
                    .suggests(SPECIES_SUGGESTIONS)
                    .executes(AncientCreatureCommand::get))));

        root.then(Commands.literal("behaviors").executes(AncientCreatureCommand::behaviors));

        root.then(Commands.literal("action")
            .then(Commands.argument("targets", EntityArgument.entities())
                .executes(AncientCreatureCommand::queryAction)
                .then(Commands.argument("action", StringArgumentType.word())
                    .suggests(ACTION_SUGGESTIONS)
                    .executes(context -> setAction(context, HOLD))
                    .then(Commands.literal("loop")
                        .executes(context -> loopAction(context, DEFAULT_LOOP_PERIOD))
                        .then(Commands.argument("period", IntegerArgumentType.integer(1, 72000))
                            .executes(context -> loopAction(context, IntegerArgumentType.getInteger(context, "period")))))
                    .then(Commands.argument("ticks", IntegerArgumentType.integer(0, 72000))
                        .executes(context -> setAction(context, IntegerArgumentType.getInteger(context, "ticks")))))));

        root.then(Commands.literal("hunger")
            .then(Commands.argument("targets", EntityArgument.entities())
                .executes(AncientCreatureCommand::queryHunger)
                .then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, SpeciesHungerProperties.MAX_HUNGER))
                    .executes(AncientCreatureCommand::setHunger))));

        root.then(Commands.literal("summon")
            .then(Commands.argument("species", IdentifierArgument.id())
                .suggests(SPECIES_SUGGESTIONS)
                .executes(context -> summon(context, context.getSource().getPosition(),
                    false, AncientCreatureEntity.DEFAULT_VARIANT))
                .then(Commands.argument("pos", Vec3Argument.vec3())
                    .executes(context -> summon(context, Vec3Argument.getVec3(context, "pos"),
                        false, AncientCreatureEntity.DEFAULT_VARIANT))
                    .then(Commands.argument("baby", BoolArgumentType.bool())
                        .executes(context -> summon(context, Vec3Argument.getVec3(context, "pos"),
                            BoolArgumentType.getBool(context, "baby"), AncientCreatureEntity.DEFAULT_VARIANT))
                        .then(Commands.argument("variant", StringArgumentType.word())
                            .executes(context -> summon(context, Vec3Argument.getVec3(context, "pos"),
                                BoolArgumentType.getBool(context, "baby"),
                                StringArgumentType.getString(context, "variant"))))))));

        dispatcher.register(root);
    }

    private static int summon(CommandContext<CommandSourceStack> context, Vec3 pos, boolean baby, String variant) {
        CommandSourceStack source = context.getSource();
        Identifier id = IdentifierArgument.getId(context, "species");

        if (SpeciesManager.SERVER.getOptional(id).isEmpty()) {
            source.sendFailure(Component.literal("No species definition loaded for '" + id + "'. Use /" + Constants.MODID + " species list to see what is available."));
            return 0;
        }

        ServerLevel level = source.getLevel();
        AncientCreatureEntity creature = ModEntities.ANCIENT_CREATURE.get().create(level, EntitySpawnReason.COMMAND);
        if (creature == null) {
            source.sendFailure(Component.literal("Could not create an ancient creature entity."));
            return 0;
        }

        creature.setSpecies(new Species(id));
        creature.setVariant(variant);
        creature.setBaby(baby);
        creature.snapTo(pos.x, pos.y, pos.z, level.getRandom().nextFloat() * 360.0F, 0.0F);

        if (!level.addFreshEntity(creature)) {
            source.sendFailure(Component.literal("Could not place " + id + " at " + String.format(java.util.Locale.ROOT, "%.2f %.2f %.2f", pos.x, pos.y, pos.z) + "."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Summoned " + (baby ? "baby " : "") + id + (AncientCreatureEntity.DEFAULT_VARIANT.equals(variant) ? "" : " (variant " + variant + ")") + " at " + String.format(java.util.Locale.ROOT, "%.2f %.2f %.2f", pos.x, pos.y, pos.z)), true);
        return 1;
    }

    /**
     * Forces the synchronised action of the selected creatures, which is what the client's animation
     * controller keys off. Purely a debugging aid: it drives the same state the server's behaviours
     * drive, so a clip can be watched on demand instead of waiting for the AI to trigger it.
     *
     * <p>With {@code ticks} omitted the action is held until it is set back to {@code none} or a
     * behaviour overwrites it. Note that a non-looping clip plays once and then leaves the model in its
     * rest pose for as long as the action stays set — that is the animation's own loop mode, not a bug.
     */
    private static int setAction(CommandContext<CommandSourceStack> context, int ticks) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "action");

        AncientCreatureAction action = parseAction(name);
        if (action == null) {
            source.sendFailure(Component.literal("Unknown action '" + name + "'. Expected one of: "
                + Arrays.stream(AncientCreatureAction.values()).map(AncientCreatureAction::queryName).collect(Collectors.joining(", "))));
            return 0;
        }

        List<AncientCreatureEntity> creatures = creatures(context);
        if (creatures.isEmpty()) {
            source.sendFailure(Component.literal("No ancient creatures matched that selector."));
            return 0;
        }

        for (AncientCreatureEntity creature : creatures) {
            // A plain set always cancels any running loop, so `... none` is a reliable stop.
            creature.setLoopAction(null, DEFAULT_LOOP_PERIOD);
            if (ticks <= HOLD) {
                creature.setAction(action);
            } else {
                creature.setAction(action, ticks);
            }
        }

        int count = creatures.size();
        String duration = ticks <= HOLD
            ? " (held until cleared with /" + Constants.MODID + " action <targets> none)"
            : " for " + ticks + " tick" + (ticks == 1 ? "" : "s");
        source.sendSuccess(() -> Component.literal("Set action " + action.queryName() + " on "
            + count + " creature" + (count == 1 ? "" : "s") + duration), true);
        return count;
    }

    /**
     * Reports how fed the selected creatures are and whether that is enough to make them hunt.
     *
     * <p>Hunger is otherwise invisible, and it moves on the order of minutes, so without this there is no
     * way to tell a predator that is ignoring prey because it is full from one that is failing to target.
     */
    private static int queryHunger(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        List<AncientCreatureEntity> creatures = creatures(context);
        if (creatures.isEmpty()) {
            source.sendFailure(Component.literal("No ancient creatures matched that selector."));
            return 0;
        }

        for (AncientCreatureEntity creature : creatures) {
            SpeciesHungerProperties hunger = creature.hungerProperties();
            String line = String.format(java.util.Locale.ROOT,
                "%s: %.1f/%.1f (hunts at %.1f, full at %.1f) - %s",
                creature.getSpecies(), creature.getHunger(), hunger.max(),
                hunger.huntThreshold(), hunger.fullThreshold(),
                creature.wantsToHunt() ? "hunting" : "not hunting");
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return creatures.size();
    }

    /** Sets hunger directly, so the hunting threshold can be crossed without waiting out the decay. */
    private static int setHunger(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        float value = FloatArgumentType.getFloat(context, "value");

        List<AncientCreatureEntity> creatures = creatures(context);
        if (creatures.isEmpty()) {
            source.sendFailure(Component.literal("No ancient creatures matched that selector."));
            return 0;
        }

        for (AncientCreatureEntity creature : creatures) {
            creature.setHunger(value);
        }

        int count = creatures.size();
        source.sendSuccess(() -> Component.literal(String.format(java.util.Locale.ROOT,
            "Set hunger to %.1f on %d creature%s", value, count, count == 1 ? "" : "s")), true);
        return count;
    }

    /**
     * Replays an action forever so a one-shot clip can actually be looked at from any angle. Stop it
     * with {@code action <targets> none}.
     */
    private static int loopAction(CommandContext<CommandSourceStack> context, int period) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "action");

        AncientCreatureAction action = parseAction(name);
        if (action == null) {
            source.sendFailure(Component.literal("Unknown action '" + name + "'. Expected one of: "
                + Arrays.stream(AncientCreatureAction.values()).map(AncientCreatureAction::queryName).collect(Collectors.joining(", "))));
            return 0;
        }

        List<AncientCreatureEntity> creatures = creatures(context);
        if (creatures.isEmpty()) {
            source.sendFailure(Component.literal("No ancient creatures matched that selector."));
            return 0;
        }

        for (AncientCreatureEntity creature : creatures) {
            creature.setLoopAction(action == AncientCreatureAction.NONE ? null : action, period);
        }

        int count = creatures.size();
        if (action == AncientCreatureAction.NONE) {
            source.sendSuccess(() -> Component.literal("Stopped looping on " + count + " creature" + (count == 1 ? "" : "s")), true);
        } else {
            source.sendSuccess(() -> Component.literal("Looping action " + action.queryName() + " every "
                + period + " ticks on " + count + " creature" + (count == 1 ? "" : "s")
                + " (stop with /" + Constants.MODID + " action <targets> none)"), true);
        }
        return count;
    }

    private static int queryAction(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<AncientCreatureEntity> creatures = creatures(context);
        if (creatures.isEmpty()) {
            context.getSource().sendFailure(Component.literal("No ancient creatures matched that selector."));
            return 0;
        }

        for (AncientCreatureEntity creature : creatures) {
            AncientCreatureAction loop = creature.getLoopAction();
            context.getSource().sendSuccess(() -> Component.literal("  " + creature.getSpecies().id()
                + " [" + creature.getStringUUID().substring(0, 8) + "] action: " + creature.getAction().queryName()
                + (loop == null ? "" : " (looping " + loop.queryName() + ")")), false);
        }
        return creatures.size();
    }

    private static List<AncientCreatureEntity> creatures(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return EntityArgument.getEntities(context, "targets").stream()
            .filter(AncientCreatureEntity.class::isInstance)
            .map(AncientCreatureEntity.class::cast)
            .toList();
    }

    private static AncientCreatureAction parseAction(String name) {
        for (AncientCreatureAction action : AncientCreatureAction.values()) {
            if (action.queryName().equalsIgnoreCase(name)) {
                return action;
            }
        }
        return null;
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        List<Species> species = SpeciesManager.SERVER.ids().stream().map(Species::new).sorted().toList();

        if (species.isEmpty()) {
            context.getSource().sendFailure(Component.literal("No species definitions loaded. Expected files under data/<namespace>/ancientcreature/species/."));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal(species.size() + " species loaded:"), false);
        for (Species entry : species) {
            SpeciesDefinition definition = SpeciesManager.SERVER.get(entry.id());
            context.getSource().sendSuccess(() -> Component.literal("  " + entry.id()
                + "  [" + definition.category().getSerializedName() + "]"
                + "  " + definition.physical().width() + "x" + definition.physical().height()), false);
        }
        return species.size();
    }

    private static int get(CommandContext<CommandSourceStack> context) {
        Identifier id = IdentifierArgument.getId(context, "id");
        SpeciesDefinition definition = SpeciesManager.SERVER.getOptional(id).orElse(null);

        if (definition == null) {
            context.getSource().sendFailure(Component.literal("No species definition loaded for '" + id + "'."));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Species " + id), false);
        context.getSource().sendSuccess(() -> Component.literal("  category: " + definition.category().getSerializedName()), false);
        context.getSource().sendSuccess(() -> Component.literal("  size: " + definition.physical().width()
            + " x " + definition.physical().height() + ", eye " + definition.physical().resolvedEyeHeight()), false);
        context.getSource().sendSuccess(() -> Component.literal("  growth: adult_age " + definition.growth().adultAge()
            + ", baby_scale " + definition.growth().babyScale()), false);
        context.getSource().sendSuccess(() -> Component.literal("  incubation: " + definition.spawn().incubationTime()
            + " ticks, biomes #" + definition.spawn().biomeTag()), false);
        context.getSource().sendSuccess(() -> Component.literal("  hunger: max " + definition.hunger().max()
            + ", hunts at " + definition.hunger().huntThreshold()
            + ", full at " + definition.hunger().fullThreshold()
            + ", -1 every " + definition.hunger().decayInterval() + " ticks"
            + (definition.hunger().starves() ? ", starves for " + definition.hunger().starveDamage() : "")), false);
        definition.attributes().values().forEach((attribute, value) ->
            context.getSource().sendSuccess(() -> Component.literal("  attribute " + attribute + " = " + value), false));
        for (CreatureBehaviorComponent component : definition.resolvedBehaviors()) {
            context.getSource().sendSuccess(() -> Component.literal("  behavior " + component.config().type()
                + " (priority " + component.priority() + ", " + component.slot().name().toLowerCase(java.util.Locale.ROOT) + ")"), false);
        }
        return 1;
    }

    private static int validate(CommandContext<CommandSourceStack> context) {
        int problems = 0;
        for (Identifier id : SpeciesManager.SERVER.ids()) {
            SpeciesDefinition definition = SpeciesManager.SERVER.get(id);
            var result = definition.validate();
            if (result.error().isPresent()) {
                problems++;
                String message = result.error().get().message();
                context.getSource().sendFailure(Component.literal("  " + id + ": " + message));
            }
        }

        int total = SpeciesManager.SERVER.ids().size();
        if (problems == 0) {
            context.getSource().sendSuccess(() -> Component.literal("All " + total + " species definitions are valid."), false);
        } else {
            int finalProblems = problems;
            context.getSource().sendFailure(Component.literal(finalProblems + " of " + total + " species definitions have problems."));
        }
        return problems == 0 ? total : 0;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Reloading datapacks to pick up species changes..."), true);
        context.getSource().getServer().reloadResources(context.getSource().getServer().getPackRepository().getSelectedIds());
        return 1;
    }

    private static int behaviors(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Behavior component types:"), false);
        for (CreatureBehaviorType<?> type : CreatureBehaviorRegistry.types()) {
            context.getSource().sendSuccess(() -> Component.literal("  " + type.id()
                + " (default priority " + type.defaultPriority() + ")"), false);
        }
        context.getSource().sendSuccess(() -> Component.literal("Behavior profiles:"), false);
        for (Identifier id : BehaviorProfiles.ids()) {
            context.getSource().sendSuccess(() -> Component.literal("  " + id), false);
        }
        return 1;
    }

    private AncientCreatureCommand() {
    }
}
