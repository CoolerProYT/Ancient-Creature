# Ancient Creature

Ancient Creature is a Minecraft mod about paleontology, fossil processing, genetic reconstruction, and bringing extinct animals back to life. Explore dig sites, recover fossils, rebuild ancient DNA, incubate embryos, and release living prehistoric creatures into the world.

> [!WARNING]
> **Ancient Creature is in early beta.** Features are incomplete and bugs are expected. Creature behavior, balance, recipes, models, animations, world generation, registry names, save data, and other systems may change between releases. Back up important worlds before updating the mod, and do not assume beta worlds will remain fully compatible with future versions.

## Current Features

- Fossil ore, rock piles, suspicious blocks, and biome-specific archaeological dig sites.
- Multiple chisels and fossil fragment types.
- A multi-stage laboratory workflow using dedicated machines:
  1. Clean recovered fossil fragments.
  2. Identify their species.
  3. Extract ancient DNA.
  4. Sequence and complete the genome.
  5. Create an embryo and fertilized ancient egg.
  6. Incubate the egg to receive a baby creature capsule.
- Fossil completeness, fossil damage, DNA integrity, and species identification systems.
- Animated machinery with custom interfaces and processing sounds.
- Baby creature capsules that remember the player who released the creature.
- Capsule-released creatures will not target or retaliate against their owner.

## Creatures

### Triceratops

A defensive herbivore that normally wanders and grazes. It becomes aggressive toward nearby hostile mobs and players carrying weapons, then bellows and charges. While charging, it can break blocks included in the `#ancientcreature:creature_destroyable` block tag when mob griefing is enabled.

### Tyrannosaurus Rex

A territorial apex predator with idle, walking, roaring, and biting animations. Adults hunt animals, hostile mobs, and survival players. A T-Rex roars when beginning an engagement, then sprints after its target and can smash tagged obstacles in its path. Babies do not hunt.

### Megalodon

A giant aquatic predator with smooth three-dimensional swimming, an articulated tail, and a bite animation. Adults hunt aquatic creatures and survival players in the water, while babies remain non-aggressive. Its model uses a more elongated reconstruction rather than simply scaling up a modern great white shark.

## Creature Ownership

Creatures released from baby creature capsules store the releasing player's UUID as their owner. Ownership persists after saving and reloading the world. Owned creatures cannot select, attack, or retaliate against their owner; this safety system does not otherwise tame them or replace their normal behavior.

Creatures created with commands or through other non-capsule methods are unowned.

## Compatibility

| Requirement | Version |
| --- | --- |
| Minecraft | 26.1 |
| Java | 25 |
| Mod loaders | Fabric and NeoForge |

The mod is developed as a multi-loader project with shared gameplay code in `common` and loader-specific entry points in `fabric` and `neoforge`.

## Installation

1. Install a compatible Fabric or NeoForge loader for Minecraft 26.1.
2. Download the Ancient Creature build made for that loader.
3. Place the mod JAR in the Minecraft `mods` directory.
4. Back up existing worlds before installing or updating early-beta builds.

Fabric and NeoForge builds are separate and cannot be used with the other loader.

## Adding Your Own Creatures

Creatures are data-driven. A species is defined by a datapack file and four resource-pack files — no Java, no mod build:

```
data/<ns>/ancientcreature/species/<name>.json                              gameplay
assets/<ns>/ancientcreature/species/<name>.json                            appearance
assets/<ns>/ancientcreature/geo/<name>.geo.json                            model
assets/<ns>/ancientcreature/animations/<name>.animation.json               animations
assets/<ns>/ancientcreature/animation_controllers/<name>.controller.json   when to play what
```

Blockbench **Bedrock Entity** and **Bedrock Animation** exports load directly. Datapack changes apply with `/reload`, resource-pack changes with F3+T.

Full documentation is in the wiki, which is maintained outside this repository — start at `guide/quick-start.md`. Other mods can add behaviour components and animation queries through the API; see `guide/api.md`. [`docs/data-driven-species.md`](docs/data-driven-species.md) summarises the system and indexes the wiki pages.

## Development

Clone the repository and import the root Gradle project using Java 25. Most shared development belongs in the `common` project.

Compile both loader targets:

```shell
./gradlew :fabric:compileJava :neoforge:compileJava
```

Build distributable JARs:

```shell
./gradlew :fabric:build :neoforge:build
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Reporting Beta Issues

When reporting a problem, include the Ancient Creature version, Minecraft version, loader and loader version, relevant logs or crash reports, and steps that reproduce the issue. Clearly state whether the world was created on the current version or upgraded from an earlier beta.

## License

Ancient Creature is licensed under [CC0 1.0 Universal](LICENSE).
