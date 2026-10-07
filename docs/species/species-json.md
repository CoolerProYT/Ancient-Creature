# Species JSON (datapack)

`data/<namespace>/ancientcreature/species/<name>.json`

Everything the **server** needs. Loaded on every `/reload` and synchronised to clients.

## Complete example

This is the shipped Triceratops, with the default `hunger` block written out.

```json
{
  "format_version": 1,
  "loot_table": "ancientcreature:entities/triceratops",
  "entity_category": "land",
  "physical": {
    "width": 1.8,
    "height": 2.2,
    "eye_height": 1.4
  },
  "attributes": {
    "max_health": 45.0,
    "movement_speed": 0.22,
    "attack_damage": 8.0,
    "attack_knockback": 1.5,
    "follow_range": 20.0,
    "knockback_resistance": 0.65
  },
  "growth": {
    "adult_age": 24000,
    "baby_scale": 0.5,
    "adult_scale": 1.0
  },
  "behavior": {
    "profile": "ancientcreature:defensive_herbivore",
    "components": []
  },
  "diet": {
    "items": ["minecraft:wheat"]
  },
  "spawn": {
    "biomes": "#minecraft:is_overworld",
    "incubation_time": 1000
  },
  "hunger": {
    "max": 20.0,
    "decay_interval": 1200,
    "hunt_threshold": 10.0,
    "full_threshold": 18.0,
    "food_value": 6.0,
    "kill_value": 8.0
  },
  "sounds": {
    "ambient": "ancientcreature:entity.triceratops.ambient",
    "hurt": "ancientcreature:entity.triceratops.hurt",
    "death": "ancientcreature:entity.triceratops.death",
    "step": "ancientcreature:entity.triceratops.step",
    "attack": "ancientcreature:entity.triceratops.attack",
    "alert": "ancientcreature:entity.triceratops.bellow",
    "ambient_interval": 240
  },
  "variants": [
    { "id": "default", "weight": 82 },
    { "id": "mottled", "weight": 18 }
  ],
  "care": {
    "climate": "temperate",
    "social": "herd",
    "group_min": 2,
    "group_max": 8
  },
  "riding": {
    "ability": "charge",
    "cooldown": 100,
    "power": 1.0
  }
}
```

## format_version

| | |
| --- | --- |
| Type | integer |
| Default | `1` |

Rejected if higher than the build understands, so a newer pack fails loudly on an older mod rather
than being half-read.

## entity_category

| | |
| --- | --- |
| Type | `"land"` \| `"aquatic"` \| `"flying"` |
| Default | `"land"` |

Selects the navigation, movement control and look control.

| Category | Gets |
| --- | --- |
| `land` | Ground pathfinding, normal movement |
| `aquatic` | Water pathfinding, smooth swimming, not pushed by currents, breathes water (see [`respiration`](#respiration)) |
| `flying` | Flight pathfinding, hovering movement control, no fall damage |

::: tip Pair it with the right components
The flight components (`ancientcreature:fly`, `ancientcreature:circle_target`) skip themselves on any
category other than `flying`, rather than pathing a walker into the air. Use the `flying_passive` or
`flying_predator` profile, or list them yourself.
:::

## physical

Collision size. Applied through the entity's dimensions, so hitbox, eye height and F3+B all follow it.

| Field | Type | Required | Default | Notes |
| --- | --- | --- | --- | --- |
| `width` | float | **yes** | — | Must be > 0, max 64 |
| `height` | float | **yes** | — | Must be > 0, max 64 |
| `eye_height` | float | no | `height * 0.85` | Vanilla's default ratio |

The box always starts at the creature's feet and is square in X/Z, so a long creature cannot be fully
enclosed — size to the body's **width**, not its length, and accept that head and tail hang outside.
That is what vanilla does for the Ravager.

::: warning Check the eye height against your model
The default (`height * 0.85`) assumes a roughly upright creature. A long horizontal one — a bipedal
predator with its spine parallel to the ground, or a fish — will get an eye floating above its own
back, and the default is easy to inherit without noticing.

Eye height is not cosmetic. Line-of-sight ray casts and `isEyeInFluid` both start there, so an eye
above the model sees over blocks the creature is standing behind, and an aquatic creature can
misjudge whether it is submerged.

Turn on F3+B and look: the red plane must cross the head. The blue arrow starts at the eye and shows
the look direction.
:::

## attributes

An open map of attribute id to base value. Any vanilla or modded attribute works; unqualified keys
default to the `minecraft` namespace, so `"max_health"` means `minecraft:max_health`.

```json
"attributes": {
  "max_health": 100.0,
  "armor": 6.0,
  "minecraft:knockback_resistance": 0.75
}
```

Commonly useful: `max_health`, `movement_speed`, `attack_damage`, `attack_knockback`, `follow_range`,
`knockback_resistance`, `armor`, `armor_toughness`, `step_height`, `safe_fall_distance`,
`jump_strength`, `gravity`, `water_movement_efficiency`, `oxygen_bonus`, `scale`.

Values are applied as **base values**, not modifiers, so reloading a datapack cannot stack duplicates.
A value outside the attribute's own range is clamped, with a warning naming the species and attribute.
An attribute id that does not exist fails validation.

::: tip Health on reload
Changing `max_health` on a live world keeps each creature at the same *fraction* of its maximum rather
than snapping it to full or leaving it over the cap.
:::

## growth

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `adult_age` | int | `24000` | Ticks for a baby to grow up; must be > 0 |
| `baby_scale` | float | `0.5` | Scales collision **and** rendering |
| `adult_scale` | float | `1.0` | Same, once grown |

## behavior

```json
"behavior": {
  "profile": "ancientcreature:defensive_herbivore",
  "components": [
    { "type": "ancientcreature:charge_attack", "priority": 1, "speed_multiplier": 1.8 }
  ]
}
```

A `profile` or a non-empty `components` list is **required** — a species with no AI is rejected,
because that is nearly always a mistake.

A component listed here replaces the profile's component of the same type, so a single goal can be
retuned without restating the rest. Full catalogue: [Behaviour components](/species/behaviors).

## diet

Used by breeding, tempting and the `tempt` component.

```json
"diet": { "items": "#ancientcreature:triceratops_food" }
```
```json
"diet": { "items": ["minecraft:wheat", "minecraft:hay_block"] }
```

Either a `#tag` string or an array of item ids. Omit `diet` entirely for a creature that neither eats
nor breeds (Megalodon does this).

## spawn

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `biomes` | `#tag` | `#minecraft:is_overworld` | Where this species' fossils generate |
| `incubation_time` | int | `1000` | Ticks for its egg to hatch |

## sounds

All optional. A missing entry simply plays nothing.

| Field | Played when |
| --- | --- |
| `ambient` | Idle, every `ambient_interval` ticks |
| `hurt` | Damaged |
| `death` | Killed |
| `step` | Walking |
| `attack` | Landing a hit |
| `alert` | Starting a charge (`charge_attack`) or a roar (`roar_attack`) |
| `ambient_interval` | Ticks between ambient sounds, default `240` |

Values are sound event ids. Your pack can register its own in `sounds.json`, or reuse vanilla ones
(`"minecraft:entity.ravager.ambient"`).

## hunger

Every species has a hunger meter, whether or not the file mentions one. It is a **fullness** meter, like
the player's food bar: it starts full, falls over time, and a *low* value means a hungry creature.

Its job is to stop predators attacking at random. A predator only picks prey once hunger falls to
`hunt_threshold`, and loses interest once eating brings it back to `full_threshold`.

```json
"hunger": {
  "max": 20.0,
  "decay_interval": 1200,
  "hunt_threshold": 10.0,
  "full_threshold": 18.0,
  "food_value": 6.0,
  "kill_value": 8.0,
  "starve_damage": 0.0,
  "starve_interval": 200
}
```

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `max` | float | `20.0` | Full meter. Matches the player food bar so the numbers read familiarly |
| `decay_interval` | int | `1200` | Ticks to lose one point. The default empties a full meter in 20 minutes |
| `hunt_threshold` | float | `10.0` | Start hunting at or below this |
| `full_threshold` | float | `18.0` | Stop hunting at or above this |
| `food_value` | float | `6.0` | Restored per item hand-fed |
| `kill_value` | float | `8.0` | Restored per prey killed |
| `starve_damage` | float | `0.0` | Damage per `starve_interval` at zero hunger. `0` disables starving |
| `starve_interval` | int | `200` | Ticks between starvation ticks |

### Why two thresholds

One threshold would sit exactly on the boundary after the first bite, and the creature would flicker
between hunting and idling every tick. The gap between `hunt_threshold` and `full_threshold` is what makes
a predator commit to a hunt and then genuinely stop. `full_threshold` must not be below `hunt_threshold`.

### What hunger does and does not gate

Hunger gates prey **selection** only. It never stops a creature defending itself.

| Gated by hunger | Never gated |
| --- | --- |
| `hunt_animals` | `defensive_retaliation` — fighting back |
| `aquatic_predator` | `hunt_hostiles` — driving off zombies is defence, not a meal |
| `territorial`, when set to `requires_hunger: true` | `melee_attack`, `charge_attack`, `roar_attack` — these carry out an attack on a target something else chose |

The attack components are deliberately never gated. If they were, a well-fed creature could not fight back
at all, which is a worse problem than attacking at random.

`territorial` defaults to **not** gated, because guarding your space is defence. But an apex predator that
sets `require_weapon: false` is not defending anything — it is hunting players on sight — so the shipped
`apex_predator` and `flying_predator` profiles set `requires_hunger: true` there. See
[Behaviour components](/species/behaviors).

### Feeding

A creature refills by:

* **killing prey** — worth `kill_value`
* **being hand-fed** a `diet` item — worth `food_value`, and it still breeds as before
* **finishing a graze** — worth the `graze` component's `hunger_value`

Grazing is how a herbivore feeds itself, which is why plant eaters never need the gate.

Hunger is saved per creature. A creature from a world saved before hunger existed starts full.

To watch it in game, use [`/ancientcreature hunger`](/species/commands#hunger). If [Jade](https://modrinth.com/mod/jade)
is installed, looking at a creature also shows its hunger and whether it is hungry enough to hunt — see
[Testing a species](/species/testing#hunger-in-jade).

## loot_table

| | |
| --- | --- |
| Type | loot table id |
| Default | none: the creature drops nothing |

What the creature drops when it dies, run through the normal entity loot pipeline, so looting,
`furnace_smelt` on fire and killer conditions all work. The shipped species each have their own table
under `data/ancientcreature/loot_table/entities/`, and the generic `large_herbivore`, `large_predator`,
`small_predator`, `giant_herbivore`, `aquatic_predator`, `flying_predator` and `arthropod` tables are
there for packs to reuse.

## variants

Weighted skins. One is rolled whenever a creature comes into being — hatching, capsule release,
breeding and `/ancientcreature summon` — and saved on the creature for good.

```json
"variants": [
  { "id": "default", "weight": 82 },
  { "id": "mottled", "weight": 18 }
]
```

| Field | Type | Required | Default |
| --- | --- | --- | --- |
| `id` | string | **yes** | — |
| `weight` | int ≥ 0 | no | `1` |

Bred babies take one parent's variant, with a 10% chance of a fresh roll. Without `variants`, every
creature is `default`.

This only picks the *id*. The resource pack decides what each id looks like: by a client-entity texture
of the same name, or through a render controller reading `query.variant` (the variant's position in this
list) or `query.variant_name`. See [Species (resource pack)](/species/client-species#variants).

## respiration

| | |
| --- | --- |
| Type | `"air"` \| `"water"` \| `"amphibious"` |
| Default | `"water"` for `aquatic`, otherwise `"air"` |

| Value | Behaviour |
| --- | --- |
| `air` | Drowns underwater like any land animal |
| `water` | Breathes through gills. On land it thrashes towards water, dries out and suffocates, like vanilla fish |
| `amphibious` | Never runs out of air in either |

Marine reptiles that surface to breathe (the shipped Mosasaurus and Plesiosaurus) use `amphibious`,
because creature AI does not surface on its own.

## care

What the species needs to be comfortable. Comfort decides whether a creature heals, breeds or — if it is
large and distressed — smashes its way out of its pen. See
[Keeping creatures](/guide/keeping-creatures#comfort) for the full effect.

```json
"care": { "climate": "cold", "social": "herd", "group_min": 3, "group_max": 10, "space": 400 }
```

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `climate` | `"any"` \| `"cold"` \| `"temperate"` \| `"warm"` | `"any"` | Judged from the base temperature of the biome the creature stands in |
| `social` | `"solitary"` \| `"pair"` \| `"herd"` | `"solitary"` | Solitary creatures dislike their own kind nearby; pairs want exactly one companion; herds want `group_min`–`group_max` of their kind within 24 blocks, counting themselves |
| `group_min` | int 1–64 | `1` | Herds only |
| `group_max` | int 1–64 | `1` | Herds only. Must not be below `group_min` |
| `space` | int 0–8192 | `0` | Walkable floor, in blocks, the creature wants to reach. `0` works it out from the width: about a 6×6 pen per square block of body, at least 24. Aquatic species count water volume; flyers are never cramped |

Climate bands, by biome base temperature:

| `climate` | Unhappy | Mildly unhappy | Happy |
| --- | --- | --- | --- |
| `cold` | above 1.0 | 0.5–1.0 | 0.5 or below |
| `warm` | below 0.3 | 0.3–0.7 | 0.7 or above |
| `temperate` | below 0.0 or above 1.5 | | everywhere else |

## riding

What happens when the rider presses the mount ability key (<kbd>R</kbd> by default).

```json
"riding": { "ability": "tail_sweep", "cooldown": 80, "power": 1.0 }
```

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `ability` | see below | `"none"` | |
| `cooldown` | int 10–12000 | `100` | Ticks between uses |
| `power` | float 0.1–10 | `1.0` | Scales damage, reach and launch speed |

| `ability` | Effect |
| --- | --- |
| `none` | Nothing; the rider is told the creature has no ability |
| `roar` | Hostile mobs within `10 × power` blocks are weakened, slowed and flee |
| `charge` | Lunges forward, hitting and throwing everything ahead, and smashes `#ancientcreature:creature_destroyable` blocks |
| `bite` | Bites the closest target ahead for 1.6× attack damage and slows it |
| `tail_sweep` | Hits everything behind and beside for 1.2× attack damage, with strong knockback |
| `stomp` | Hits and slows everything on the ground close by |
| `pounce` | Leaps forward and strikes whatever it lands on |
| `dive` | Flyers swoop down and strike on contact; swimmers get a burst of speed that rams what's ahead |

Damage is based on the creature's `attack_damage` (at least 2), times `power`. Abilities never hit the
rider, the rider's other creatures, or anything the rider could not attack. Riding itself works for
every species whether or not it has an ability; see [Riding creatures](/guide/riding).

## hybrid

Marks the species as a hybrid of two others.

```json
"hybrid": { "parents": ["ancientcreature:stegosaurus", "ancientcreature:triceratops"] }
```

| Field | Type | Notes |
| --- | --- | --- |
| `parents` | two species ids | Must name two different species |

A hybrid:

* never appears as fossils, whatever `spawn.biomes` says, and has no DNA or cartridge items of its own,
* is made in the Embryogenesis Chamber from a completed genome of each parent, one in the genome slot and
  one in the donor slot, in either order,
* keeps 85% of the parents' average genome fidelity, and is always sterile,
* is added to the Species Journal the first time one is made.

The shipped Stegoceratops and Tyrannoraptor are hybrids.

## Validation

Rejected at load, with the file named in the log:

* non-positive or non-finite `width` / `height`
* an attribute id that does not exist
* an unknown `behavior.profile`
* an unknown behavior component `type` (the error lists every valid one)
* a `hunt_threshold` above `max`, or a `full_threshold` below `hunt_threshold`
* a `care.group_max` below `care.group_min`
* `hybrid.parents` that is not two different species ids
* an unknown `respiration`, `care.climate`, `care.social` or `riding.ability`, or a number outside its range
* an unsupported `format_version`
* a malformed resource id anywhere
* no behaviour at all

A file that fails is skipped; every other species still loads. The loaded set is only swapped in after
the whole reload succeeds, so the game never sees a half-applied state.
