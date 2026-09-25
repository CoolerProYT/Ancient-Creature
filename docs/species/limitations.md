# Limitations

Honest list of what does not work yet, and why.

## Per-face UV is approximated

Minecraft's cube builder has no per-face UV entry point. A cube using the per-face form is accepted but
its UV is guessed from the smallest face corner, with a warning. The texture will very likely be wrong.

**Workaround:** export with box UV. In Blockbench, *File → Project → UV Mode → Box UV*.

## No Molang

Keyframe values must be literal numbers or vectors — there is no expression evaluator for them.

Animation *controller* conditions do support a
[documented subset](/species/animation-controllers#expression-grammar) of Molang-style syntax, but not
functions (`math.sin`), variables (`variable.x`) or arithmetic. Anything outside the grammar is a
load-time error rather than a silently dead transition.

## Texture variants are not rolled on spawn

Variants are fully plumbed — parsed, stored on the entity, synchronised, persisted, and used when
choosing the texture — but nothing currently assigns one at spawn time. Every creature uses `default`
unless a variant is set explicitly:

```
/ancientcreature summon mypack:stegosaurus ~ ~ ~ false albino
```

## `clientTrackingRange` and `updateInterval` cannot be data-driven

They are `EntityType` properties, frozen at registration, and there is one shared type. The generic type
uses a tracking range of 12 chunks, generous enough for large species.

## Natural spawning is not data-driven

`spawn.biomes` controls where a species' **fossils** generate, not where live creatures spawn. Creatures
come from the fossil → DNA → embryo → egg chain. There is no natural spawn weight or spawn rule in the
species format.

## Aquatic creatures do not suffocate on land

The old Megalodon was a `WaterAnimal`, which takes damage out of water. The generic creature does not
replicate that. An aquatic species will flop on land indefinitely rather than dying. The
`ancientcreature:find_water` component makes it head back to water.

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
[Migration](/species/migration#why-the-old-entity-ids-still-exist).

## Creative tab and JEI before joining a world

The client only learns which species exist when it joins a server, so a listing built before that shows
none. Both are rebuilt on join, so this is not visible in normal play.

## Not verified in-game by automated tests

Automated tests cover the JSON formats, the codecs, the geometry conversion, the behaviour registry and
the expression parser. They do **not** launch Minecraft. Anything involving actual rendering, pathing or
gameplay needs the manual pass in [Testing a species](/species/testing).
