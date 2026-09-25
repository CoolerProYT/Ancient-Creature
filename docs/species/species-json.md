# Species JSON (datapack)

`data/<namespace>/ancientcreature/species/<name>.json`

Everything the **server** needs. Loaded on every `/reload` and synchronised to clients.

## Complete example

This is the shipped Triceratops, unabridged.

```json
{
  "format_version": 1,
  "entity_category": "land",
  "physical": {
    "width": 1.2,
    "height": 1.8,
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
| `aquatic` | Water pathfinding, smooth swimming, underwater breathing, not pushed by currents |
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

## Validation

Rejected at load, with the file named in the log:

* non-positive or non-finite `width` / `height`
* an attribute id that does not exist
* an unknown `behavior.profile`
* an unknown behavior component `type` (the error lists every valid one)
* a `hunt_threshold` above `max`, or a `full_threshold` below `hunt_threshold`
* an unsupported `format_version`
* a malformed resource id anywhere
* no behaviour at all

A file that fails is skipped; every other species still loads. The loaded set is only swapped in after
the whole reload succeeds, so the game never sees a half-applied state.
