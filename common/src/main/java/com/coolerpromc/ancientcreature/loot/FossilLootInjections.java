package com.coolerpromc.ancientcreature.loot;

import com.coolerpromc.ancientcreature.Constants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fossils hidden in vanilla loot: brushing vanilla ruins, fishing, and some mob drops.
 *
 * <p>Each injection rolls one of this mod's own loot tables (under {@code ancientcreature:inject/})
 * alongside a vanilla table, so the chances live in datapack JSON and can be tuned or emptied there.
 * Both loaders feed generated drops through {@link #apply}: NeoForge from a global loot modifier,
 * Fabric from its drop-modification event.
 */
public final class FossilLootInjections {
    /** What to do with the vanilla drops when the injected table yields something. */
    public enum Mode {
        /** Swap the drop out entirely: brushing and fishing hand out exactly one item. */
        REPLACE,
        /** Drop the fossil on top of the usual loot. */
        APPEND
    }

    public record Injection(ResourceKey<LootTable> source, Mode mode) {
    }

    public static final ResourceKey<LootTable> ARCHAEOLOGY_DESERT = key("inject/archaeology/desert");
    public static final ResourceKey<LootTable> ARCHAEOLOGY_TRAIL_RUINS = key("inject/archaeology/trail_ruins");
    public static final ResourceKey<LootTable> ARCHAEOLOGY_TRAIL_RUINS_RARE = key("inject/archaeology/trail_ruins_rare");
    public static final ResourceKey<LootTable> ARCHAEOLOGY_OCEAN_RUIN = key("inject/archaeology/ocean_ruin");
    public static final ResourceKey<LootTable> FISHING = key("inject/gameplay/fishing");
    public static final ResourceKey<LootTable> HUSK = key("inject/entities/husk");
    public static final ResourceKey<LootTable> DROWNED = key("inject/entities/drowned");

    private static final Map<Identifier, List<Injection>> INJECTIONS = new HashMap<>();

    static {
        inject("archaeology/desert_pyramid", ARCHAEOLOGY_DESERT, Mode.REPLACE);
        inject("archaeology/desert_well", ARCHAEOLOGY_DESERT, Mode.REPLACE);
        inject("archaeology/trail_ruins_common", ARCHAEOLOGY_TRAIL_RUINS, Mode.REPLACE);
        inject("archaeology/trail_ruins_rare", ARCHAEOLOGY_TRAIL_RUINS_RARE, Mode.REPLACE);
        inject("archaeology/ocean_ruin_warm", ARCHAEOLOGY_OCEAN_RUIN, Mode.REPLACE);
        inject("archaeology/ocean_ruin_cold", ARCHAEOLOGY_OCEAN_RUIN, Mode.REPLACE);
        inject("gameplay/fishing", FISHING, Mode.REPLACE);
        inject("entities/husk", HUSK, Mode.APPEND);
        inject("entities/drowned", DROWNED, Mode.APPEND);
    }

    private FossilLootInjections() {
    }

    public static boolean hasInjections(@Nullable Identifier tableId) {
        return tableId != null && INJECTIONS.containsKey(tableId);
    }

    @SuppressWarnings("deprecation") // The raw roll is deliberate: the injected table must not run the loot modifiers again.
    public static void apply(@Nullable Identifier tableId, LootContext context, List<ItemStack> drops) {
        if (tableId == null) {
            return;
        }
        List<Injection> injections = INJECTIONS.get(tableId);
        if (injections == null) {
            return;
        }

        for (Injection injection : injections) {
            LootTable source = context.getLevel().getServer().reloadableRegistries().getLootTable(injection.source());
            ObjectArrayList<ItemStack> extra = new ObjectArrayList<>();
            source.getRandomItemsRaw(context, stack -> {
                if (!stack.isEmpty()) {
                    extra.add(stack);
                }
            });
            if (extra.isEmpty()) {
                continue;
            }
            if (injection.mode() == Mode.REPLACE) {
                drops.clear();
            }
            drops.addAll(extra);
        }
    }

    public static List<ResourceKey<LootTable>> sources() {
        List<ResourceKey<LootTable>> sources = new ArrayList<>();
        INJECTIONS.values().forEach(list -> list.forEach(i -> {
            if (!sources.contains(i.source())) {
                sources.add(i.source());
            }
        }));
        return sources;
    }

    private static void inject(String vanillaTable, ResourceKey<LootTable> source, Mode mode) {
        INJECTIONS.computeIfAbsent(Identifier.withDefaultNamespace(vanillaTable), k -> new ArrayList<>()).add(new Injection(source, mode));
    }

    private static ResourceKey<LootTable> key(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(path));
    }
}
