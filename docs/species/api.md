# API for mod developers

Other mods can extend the species system: add behaviour components that any species JSON can then use,
add animation queries, read species data, and spawn creatures.

Two entry points:

| Class | Side | For |
| --- | --- | --- |
| `com.coolerpromc.ancientcreature.api.AncientCreatureApi` | common | Behaviours, species data, spawning |
| `com.coolerpromc.ancientcreature.api.client.AncientCreatureClientApi` | client | Animation queries, geometry |

Everything reachable from those two is meant to stay source-compatible. Anything outside
`…ancientcreature.api` is internal.

::: warning Register during mod initialization
The behaviour registries freeze the first time species data is parsed. Registering later throws with an
explanation — because species files loaded before your registration would already have been rejected
for using an "unknown" component type.
:::

## A custom behaviour component

Implement `CreatureBehaviorConfig`: an immutable record, a `MapCodec` for its fields, and a factory
producing a vanilla `Goal`.

```java
public record DigBehavior(int duration, double speedModifier) implements CreatureBehaviorConfig {

    public static final MapCodec<DigBehavior> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.INT.optionalFieldOf("duration", 40).forGetter(DigBehavior::duration),
        Codec.DOUBLE.optionalFieldOf("speed_modifier", 1.0).forGetter(DigBehavior::speedModifier)
    ).apply(i, DigBehavior::new));

    public static CreatureBehaviorType<DigBehavior> TYPE;

    @Override
    public CreatureBehaviorType<?> type() {
        return TYPE;
    }

    @Override
    public Goal createGoal(AncientCreatureEntity entity) {
        // Return null to opt out for this entity - e.g. if the species is aquatic.
        return new MyDigGoal(entity, this.duration, this.speedModifier);
    }

    // Optional: put it in the target selector instead of the goal selector.
    // @Override public GoalSlot slot() { return GoalSlot.TARGET; }
}
```

Register it:

```java
DigBehavior.TYPE = AncientCreatureApi.registerBehavior(
    Identifier.fromNamespaceAndPath("mymod", "dig"),
    DigBehavior.CODEC,
    5); // default priority when JSON omits "priority"
```

Any species can now use it — including species from packs that know nothing about your mod:

```json
{
  "behavior": {
    "profile": "ancientcreature:passive",
    "components": [
      { "type": "mymod:dig", "priority": 5, "duration": 60, "speed_modifier": 1.2 }
    ]
  }
}
```

### What your goal gets

`AncientCreatureEntity` exposes what a behaviour usually needs:

```java
entity.getSpecies()          // Species (an Identifier wrapper)
entity.speciesDefinition()   // SpeciesDefinition, never null
entity.speciesCategory()     // LAND / AQUATIC / FLYING
entity.speciesSounds()       // resolved sound events, may be null individually
entity.diet()                // Predicate<ItemStack>
entity.isOwnedBy(entity)     // owner check
entity.setAction(action, ticks)  // publish an action for the client's animation controller
```

## A custom profile

```java
AncientCreatureApi.registerBehaviorProfile(
    Identifier.fromNamespaceAndPath("mymod", "burrower"),
    List.of(
        AncientCreatureApi.component(FloatBehavior.INSTANCE),
        AncientCreatureApi.component(new DigBehavior(60, 1.2), 2),
        AncientCreatureApi.component(new WanderBehavior(0.9, true, 120))
    ));
```

Species using it can still override any single component by listing it.

## Reading species data

```java
Optional<SpeciesDefinition> def = AncientCreatureApi.getSpecies(id);
boolean loaded = AncientCreatureApi.isSpeciesLoaded(id);
List<Identifier> all = AncientCreatureApi.loadedSpecies();
Optional<Identifier> species = AncientCreatureApi.speciesOf(someEntity);
```

`getSpecies` reads server data where available and falls back to the client's synchronised copy, so it
works from either side.

To invalidate anything you derive from species data:

```java
AncientCreatureApi.onSpeciesReloaded(() -> myCache.clear());
```

Called on the server thread after every successful reload, including the first load. An exception in
your listener is logged and the rest still run.

## Spawning

```java
AncientCreatureEntity creature = AncientCreatureApi.spawn(
    serverLevel,
    Identifier.fromNamespaceAndPath("mypack", "stegosaurus"),
    pos,
    /* baby = */ true,
    /* owner = */ player);
```

The species is applied before the entity enters the world, so it is placed using its own bounding box
rather than the generic default. Returns `null` if it could not be placed.

## Client: animation queries

```java
AncientCreatureClientApi.registerNumberQuery("wing_beat",
    (state, secondsInState) -> Math.sin(state.ageInTicks * 0.3) * 0.5 + 0.5);

AncientCreatureClientApi.registerStringQuery("mood",
    (state, secondsInState) -> state.creatureHealth < 5 ? "afraid" : "calm");
```

Usable from any controller:

```json
{ "glide": "query.wing_beat > 0.8" }
{ "cower": "query.mood == 'afraid'" }
```

Names clashing with a built-in query are rejected. Queries only *read* the render state — a controller
still cannot change gameplay.

## Client: geometry

```java
ModelPart baked = AncientCreatureClientApi.getBakedGeometry(geometryId);
boolean present = AncientCreatureClientApi.hasGeometry(geometryId);
int generation = AncientCreatureClientApi.geometryGeneration();
```

`geometryGeneration()` is bumped on every resource reload — compare against a stored value to know when
to drop anything you cached from a baked model.

## Version check

```java
if (AncientCreatureApi.speciesFormatVersion() < 1) {
    // built against a newer format than this Ancient Creature understands
}
```
