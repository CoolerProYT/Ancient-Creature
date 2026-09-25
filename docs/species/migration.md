# Migration & compatibility

## For players

::: tip Existing creatures are preserved
The old `ancientcreature:triceratops`, `ancientcreature:tyrannosaurus_rex` and
`ancientcreature:megalodon` entity ids remain registered as compatibility shims. Creatures already
placed in an older world therefore survive the migration and load with the data-driven implementation.
:::

Everything that is not a live creature loads unchanged. The old species representation saved as a bare
name (`"triceratops"`), and the replacement reads that correctly, so every fossil, DNA sample, genome
cartridge, fertilized egg, capsule and identified-species entry in your world still works. Hatch a
fertilized egg or pop a capsule and you get the same creature back, now data-driven.

::: warning One-way
Species values are now written fully qualified (`ancientcreature:triceratops`). Older versions of the
mod cannot read those. Downgrading after loading a world will lose species data.
:::

## For pack authors

Nothing to migrate — the species system is new. Start at [Add a species](/species/quick-start).

## For mod developers

If you referenced the old classes, they are gone:

| Removed | Replacement |
| --- | --- |
| `Triceratops`, `TyrannosaurusRex`, `Megalodon` | `AncientCreatureEntity` |
| `OwnedWaterAncientCreature` | `AncientCreatureEntity` with `entity_category: "aquatic"` |
| `TriceratopsModel`, `TriceratopsRenderer`, `TriceratopsRenderState`, `TriceratopsAnimation` (and the T-Rex / Megalodon equivalents) | The generic renderer plus resource-pack files |
| `ModModelLayers.TRICERATOPS` and friends | None — creatures use no model layers |
| `Species` enum constants | `Species` is now a record over an `Identifier`; `Species.TRICERATOPS` still exists as a well-known id |
| `ModEntities.TRICERATOPS`, `.TYRANNOSAURUS_REX`, `.MEGALODON` for new spawns | `ModEntities.ANCIENT_CREATURE`, or `AncientCreatureApi.creatureType()` |

::: danger Reference equality
`Species` was an enum, so `species == Species.MEGALODON` worked. It is a record now — use
`.equals(...)`. This compiles either way and silently returns `false`.
:::

Use the [API](/species/api) rather than internals where you can:

```java
AncientCreatureApi.speciesOf(entity)                 // Optional<Identifier>
AncientCreatureApi.getSpecies(id)                    // Optional<SpeciesDefinition>
AncientCreatureApi.spawn(level, id, pos, baby, owner)
AncientCreatureApi.creatureType()                    // the one EntityType
```

## Why the old entity ids remain

Nothing creates them any more — breeding, eggs and capsules all produce
`ancientcreature:ancient_creature`. The old registrations exist only so Minecraft can deserialize live
creatures saved before the migration; removing them would make those entities disappear when their
chunks load. Pack authors and new code should always use the generic entity plus a species id.

## Design decisions worth knowing

**Species data is a reload listener, not a datapack registry.** Datapack registries are built once at
world load and are not re-read by `/reload`, which is the whole iteration loop this system needs. The
cost is one sync packet, which is needed anyway.

**Attributes are base values, not modifiers.** Setting a base value is idempotent, so reloading a
datapack any number of times cannot stack duplicates and nothing needs reapplying per tick.

**One entity type, not three.** `Mob.navigation`, `moveControl` and `lookControl` are protected,
non-final fields, so the movement domain is swapped when the species is applied rather than baked into
the type. Land, aquatic and flying creatures share one registration.

**Unresolved species stay unresolved.** A creature whose species id has no definition keeps the id
verbatim, registers no AI and uses safe defaults. It is never rewritten to a different species, and it
repairs itself if the definition comes back.
