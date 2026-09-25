# Commands

All require permission level 2 (gamemaster) — the same tier as `/reload`.

## Species

### `/ancientcreature species list`

Every loaded species with its movement category and size.

```
6 species loaded:
  ancientcreature:ankylosaurus  [land]  1.9x1.45
  ancientcreature:deinonychus  [land]  0.95x1.45
  ancientcreature:megalodon  [aquatic]  2.8x1.6
  ancientcreature:pteranodon  [flying]  1.5x1.4
  ancientcreature:triceratops  [land]  1.8x2.2
  ancientcreature:tyrannosaurus_rex  [land]  1.2x2.0
```

If nothing is listed, the datapack half is not loaded. The message tells you where the files belong.

### `/ancientcreature species get <id>`

Everything resolved for one species — including the behaviour list *after* the profile has been
expanded and your overrides applied, which is the quickest way to check a profile did what you meant.

```
Species ancientcreature:triceratops
  category: land
  size: 1.8 x 2.2, eye 1.4
  growth: adult_age 24000, baby_scale 0.5
  incubation: 1000 ticks, biomes #minecraft:is_overworld
  hunger: max 20.0, hunts at 10.0, full at 18.0, -1 every 1200 ticks
  attribute minecraft:max_health = 45.0
  ...
  behavior ancientcreature:charge_attack (priority 1, goal)
  behavior ancientcreature:territorial (priority 2, target)
  ...
```

Tab-completes loaded species ids.

### `/ancientcreature species validate`

Re-runs validation over everything loaded and reports what fails. Useful after editing a pack by hand.

### `/ancientcreature species reload`

Reloads datapacks, picking up species changes. Equivalent to `/reload`, and provided so you do not have
to remember which one refreshes species.

## Behaviour

### `/ancientcreature behaviors`

Every registered component type with its default priority, and every profile — including any added by
other mods. The authoritative list for the version you are running.

## Hunger

### `/ancientcreature hunger <targets>`

Reports how fed the selected creatures are, and whether that is enough to make them hunt.

```
ancientcreature:tyrannosaurus_rex: 14.0/20.0 (hunts at 10.0, full at 18.0) - not hunting
```

Hunger is otherwise invisible and moves on the order of minutes, so this is the way to tell a predator
that is ignoring prey because it is **full** from one that is failing to target at all.

### `/ancientcreature hunger <targets> <value>`

Sets hunger directly, so you can cross the hunting threshold without waiting out the decay.

```
/ancientcreature hunger @e[type=ancientcreature:ancient_creature,limit=1] 0
```

That is the quickest way to confirm a predator does start hunting — drop it to zero and watch it pick a
target. Set it back to `20` and it should lose interest once it climbs past `full_threshold`.

See [hunger](/species/species-json#hunger) for what the numbers mean.

## Actions

### `/ancientcreature action <targets> [<action>] [<ticks>|loop [<period>]]`

Forces the synchronised action that the client's animation controller keys off, so a clip can be watched
on demand instead of waiting for the AI to trigger it. With no action, it reports the current one.

```
/ancientcreature action @e[limit=1] roar
/ancientcreature action @e[limit=1] eat 40
/ancientcreature action @e[limit=1] roar loop
/ancientcreature action @e[limit=1] none
```

With `ticks` omitted the action is held until something clears it; `none` always stops a loop. Note that a
non-looping clip plays once and then leaves the model in its rest pose for as long as the action stays
set — that is the animation's own loop mode, not a bug.

## Spawning

### `/ancientcreature summon <species> [<pos>] [<baby>] [<variant>]`

```
/ancientcreature summon mypack:stegosaurus
/ancientcreature summon mypack:stegosaurus ~ ~ ~
/ancientcreature summon mypack:stegosaurus ~ ~ ~ true
/ancientcreature summon mypack:stegosaurus ~ ~ ~ false albino
```

Tab-completes loaded species ids, and fails with a clear message if the id has no definition rather
than spawning an inert creature.

The species is applied before the creature is placed, so it is positioned using its own bounding box
and spawns at its own maximum health — not the generic 20.

::: tip Why not `/summon`
Every species shares one entity type, so `/summon ancientcreature:ancient_creature` gives you the
default species unless you hand-write the NBT:

```
/summon ancientcreature:ancient_creature ~ ~ ~ {Species:"mypack:stegosaurus",Variant:"albino",Age:-24000}
```

That still works — `Age` follows vanilla, negative is a baby — but it does not tab-complete and it
does not tell you when the id is wrong.
:::

## Log lines worth knowing

On a successful load you should see:

```
Loaded 6 Ancient Creature species definitions (server side)
Loaded 6 Ancient Creature client species definitions
Loaded 6 Bedrock geometries
Loaded 32 Bedrock animations from 6 files
Loaded 6 Ancient Creature animation controllers
```

Problems are logged with the file path and the reason:

```
Invalid Ancient Creature species definition mypack:stegosaurus (...): unknown attribute(s): minecraft:speed
Client species 'mypack:stegosaurus' references missing geometry 'mypack:stego'
Animation 'animation.stego.walk' targets bone 'leg1', which the geometry does not have
No species definition for 'mypack:stegosaurus'. Creatures of this species will stay inert ...
```
