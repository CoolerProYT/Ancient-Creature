# Limitations

Honest list of what does not work yet, and why.

## Bedrock features with no Java counterpart

The resource-pack side reads real Bedrock files, but a few things only exist in Bedrock:

* **Materials** are not Bedrock's shader materials. A material name only picks how a layer is drawn:
  translucent if it contains `alphablend`, `translucent` or `blend`, full-bright if it contains
  `emissive`, cutout otherwise.
* **Particle effects** play a Java particle with no options, or one of a handful of Bedrock built-ins
  mapped to their Java equivalents. Bedrock particle effect files are not read.
* **Entity references** (`->`) parse but give 0, and `query.is_first_person` is always 0.

## `clientTrackingRange` and `updateInterval` cannot be data-driven

They are `EntityType` properties, frozen at registration, and there is one shared type. The generic type
uses a tracking range of 12 chunks, generous enough for large species.

## Natural spawning is not data-driven

`spawn.biomes` controls where a species' **fossils** generate, not where live creatures spawn. Creatures
come from the fossil → DNA → embryo → egg chain. There is no natural spawn weight or spawn rule in the
species format.

## Flying has no aerodynamic simulation

`entity_category: "flying"` gives real flight navigation, hovering movement control and no fall damage,
plus the `fly` and `circle_target` components. The `fly` component supports configurable minimum and
maximum altitude, wander range and vertical range. Animation controllers can distinguish grounded and
airborne states, so Pteranodon folds its wings while perched and switches between powered flight and a
gliding pose.

This is still path-based flight rather than an aerodynamic simulation: lift, stalls, wind and momentum
are not modelled, and landing is not a separate navigation phase.

## Legacy entity ids linger

`ancientcreature:triceratops`, `:tyrannosaurus_rex` and `:megalodon` remain registered so existing
worlds keep their creatures. They are data-driven now and nothing spawns them, but the registrations
cannot be removed without deleting those creatures from old worlds. See
[Migration](/species/migration#why-the-old-entity-ids-remain).

## Creative tab and JEI before joining a world

The client only learns which species exist when it joins a server, so a listing built before that shows
none. Both are rebuilt on join, so this is not visible in normal play.

## Not verified in-game by automated tests

Automated tests cover the JSON formats, the codecs, the geometry baker, animation sampling, Molang, the
behaviour registry, genetics and every shipped species file. They do **not** launch Minecraft. Anything involving actual rendering, pathing or
gameplay needs the manual pass in [Testing a species](/species/testing).
