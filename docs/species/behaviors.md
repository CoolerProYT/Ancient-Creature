# Behaviour components

A species' AI is a list of components. A pack picks from a fixed catalogue and configures each one; it
cannot introduce new Java behaviour.

## Profiles

Most creatures need only a profile — a named bundle of components.

```json
"behavior": { "profile": "ancientcreature:defensive_herbivore" }
```

| Profile | Shape | Used by |
| --- | --- | --- |
| `ancientcreature:passive` | Wanders, panics when hurt, breeds, is tempted by food, follows a parent | — |
| `ancientcreature:defensive_herbivore` | Passive until threatened, then bellows and charges. Attacks armed players who come close, and hostile mobs | Triceratops |
| `ancientcreature:armored_herbivore` | Slow grazer that herds and stands its ground. Retaliates with melee, alerts its herd, and never charges | Ankylosaurus |
| `ancientcreature:pack_predator` | Fast social predator that stays near its kind, calls before chasing and hunts only when hungry. Adults only | Deinonychus |
| `ancientcreature:sauropod_browser` | Towering herd herbivore that seeks canopy leaves, browses without breaking them and retaliates with melee | Brachiosaurus |
| `ancientcreature:apex_predator` | Roars, then charges. Hunts players and animals **when hungry**, hostiles always. Adults only | Tyrannosaurus Rex |
| `ancientcreature:aquatic_predator` | Swims, seeks water when beached, hunts anything in the water **when hungry**. No breeding | Megalodon |
| `ancientcreature:flying_passive` | Flies, is tempted down by food, breeds, does not fight back | — |
| `ancientcreature:flying_predator` | Circles its target, then dives. Hunts animals and players **when hungry** | Pteranodon |

::: tip Predator profiles wait until they are hungry
The three predator profiles gate their prey selection on [hunger](/species/species-json#hunger), so they no
longer attack everything that walks past. Retaliation and hostile-mob defence are never gated. Feed a
creature, or use `/ancientcreature hunger`, to see the difference.
:::

::: tip Flying profiles need the flying category
`flying_passive` and `flying_predator` only work with `"entity_category": "flying"`. On any other
category the flight components skip themselves and you get a creature that walks.
:::

### Overriding one piece

A component you list replaces the profile's component of the same type. Everything else stays.

```json
"behavior": {
  "profile": "ancientcreature:defensive_herbivore",
  "components": [
    { "type": "ancientcreature:charge_attack", "speed_multiplier": 1.8, "break_blocks": false }
  ]
}
```

That keeps the whole herbivore kit and only retunes the charge.

### Building from scratch

Omit `profile` and list everything:

```json
"behavior": {
  "components": [
    { "type": "ancientcreature:float" },
    { "type": "ancientcreature:wander", "speed_modifier": 0.9 },
    { "type": "ancientcreature:look_at_player" },
    { "type": "ancientcreature:random_look" }
  ]
}
```

## Priority

Every component takes an optional `"priority"`. Lower numbers win when goals conflict — standard
Minecraft goal priority. Omit it to use the type's default.

```json
{ "type": "ancientcreature:charge_attack", "priority": 1, "speed_multiplier": 1.45 }
```

## Movement and idle

### `ancientcreature:float` <Badge type="info" text="priority 0" />
Keeps a land creature's head above water. Automatically skipped for aquatic species. No options.

### `ancientcreature:find_water` <Badge type="info" text="priority 0" />
Sends a beached aquatic creature back towards water. No options.

### `ancientcreature:panic` <Badge type="info" text="priority 1" />
Runs away after being hurt.

| Option | Default |
| --- | --- |
| `speed_modifier` | `1.25` |

### `ancientcreature:wander` <Badge type="info" text="priority 6" />
Idle strolling. Uses a swimming variant automatically for aquatic species.

| Option | Default | Notes |
| --- | --- | --- |
| `speed_modifier` | `0.9` | |
| `avoid_water` | `true` | Land species only |
| `interval` | `120` | Ticks between attempts |

### `ancientcreature:swimming` <Badge type="info" text="priority 6" />
Open-water wandering.

| Option | Default |
| --- | --- |
| `speed_modifier` | `1.0` |
| `interval` | `10` |

### `ancientcreature:fly` <Badge type="info" text="priority 6" />
Free flight. Requires `"entity_category": "flying"`; skipped otherwise.

| Option | Default |
| --- | --- |
| `speed_modifier` | `1.0` |

### `ancientcreature:look_at_player` <Badge type="info" text="priority 7" />

| Option | Default |
| --- | --- |
| `look_distance` | `8.0` |
| `probability` | `0.02` |

### `ancientcreature:random_look` <Badge type="info" text="priority 8" />
Idle head movement. No options.

## Social

### `ancientcreature:breed` <Badge type="info" text="priority 3" />
Pairing up to breed. Only ever with the same species — two different data-driven creatures sharing the
generic entity type never cross-breed.

| Option | Default |
| --- | --- |
| `speed_modifier` | `1.0` |

### `ancientcreature:tempt` <Badge type="info" text="priority 4" />
Follows a player holding food. The item set comes from the species' `diet` block; the component does
nothing if `diet` is empty.

| Option | Default |
| --- | --- |
| `speed_modifier` | `1.15` |
| `can_scare` | `false` |

### `ancientcreature:follow_parent` <Badge type="info" text="priority 5" />

| Option | Default |
| --- | --- |
| `speed_modifier` | `1.1` |

### `ancientcreature:herding` <Badge type="info" text="priority 6" />
Keeps members of the same species loosely together.

| Option | Default | Notes |
| --- | --- | --- |
| `speed_modifier` | `1.0` | |
| `search_range` | `24.0` | How far to look for a herd mate |
| `join_distance` | `12.0` | Start moving closer beyond this |
| `stop_distance` | `5.0` | Stop once this close |

### `ancientcreature:graze` <Badge type="info" text="priority 5" />
Periodically stops to graze, publishing the `graze` action so the animation controller can react. Not
used by babies, targets or creatures in water.

| Option | Default | Notes |
| --- | --- | --- |
| `duration` | `80` | Ticks |
| `chance` | `400` | Reciprocal chance per tick |
| `hunger_value` | `4.0` | Hunger restored by finishing a graze |

Hunger is only awarded when the graze runs to completion, so a mouthful interrupted by a threat is not a
free meal. This is how a herbivore feeds itself. See [hunger](/species/species-json#hunger).

### `ancientcreature:browse_leaves` <Badge type="info" text="priority 5" />
Searches for leaf blocks above ground level, walks beneath the nearest reachable canopy, looks up and
publishes the `graze` action while eating. It restores hunger only after the browsing animation finishes
and never removes the leaf block.

| Option | Default | Notes |
| --- | --- | --- |
| `duration` | `100` | Ticks spent eating |
| `chance` | `300` | Reciprocal chance per tick |
| `horizontal_range` | `7` | Canopy search radius in blocks |
| `min_height` | `3` | Lowest leaf height above the creature's feet |
| `max_height` | `8` | Highest leaf height considered |
| `speed_modifier` | `0.7` | Approach speed |
| `reach_distance` | `2.75` | Horizontal distance at which eating begins |
| `hunger_value` | `8.0` | Hunger restored by completing the browse |

## Attacking

### `ancientcreature:melee_attack` <Badge type="info" text="priority 2" />
Plain chase-and-bite.

| Option | Default |
| --- | --- |
| `speed_modifier` | `1.2` |
| `follow_without_sight` | `true` |
| `adults_only` | `false` |

### `ancientcreature:charge_attack` <Badge type="info" text="priority 1" />
Sprints at the target, plays the species' `alert` sound on engaging, and optionally ploughs through
blocks tagged `#ancientcreature:creature_destroyable` (mob griefing must be on).

| Option | Default |
| --- | --- |
| `speed_multiplier` | `1.45` |
| `follow_without_sight` | `true` |
| `break_blocks` | `true` |
| `break_reach` | `0.6` |

### `ancientcreature:roar_attack` <Badge type="info" text="priority 1" />
Opens with a roar: stops, tracks the target and plays the species' `alert` sound for `roar_duration`
ticks, then charges. Publishes the `roar` action for the animation controller, and goes on cooldown so
it does not fire on every re-engage.

| Option | Default |
| --- | --- |
| `speed_multiplier` | `1.35` |
| `follow_without_sight` | `true` |
| `roar_duration` | `48` |
| `roar_cooldown` | `240` |
| `break_blocks` | `true` |
| `break_reach` | `1.0` |
| `adults_only` | `true` |

### `ancientcreature:circle_target` <Badge type="info" text="priority 1" />
Holds altitude and orbits the current target, then releases after `dive_after` ticks so a melee
component can take over. Requires `"entity_category": "flying"`; skipped otherwise, and skipped for
babies.

| Option | Default | Notes |
| --- | --- | --- |
| `speed_modifier` | `1.1` | |
| `radius` | `8.0` | Orbit radius in blocks |
| `height` | `6.0` | Blocks above the target |
| `dive_after` | `100` | Ticks of circling before releasing |

## Choosing targets

These go in the mob's *target* selector rather than its goal selector; the slot is chosen
automatically.

Predatory components here are gated on [hunger](/species/species-json#hunger): they only pick a target when
the creature is actually hungry, and stop once it has eaten. Defensive ones are never gated, so a well-fed
creature still fights back. Each component below states its own default.

The attack components under [Attacking](#attacking) are never gated either. They carry out an attack on
whatever target these components chose, so gating them would leave a fed creature unable to defend
itself.

### `ancientcreature:defensive_retaliation` <Badge type="info" text="priority 1" />
Fights back against whatever hurt it.

| Option | Default | Notes |
| --- | --- | --- |
| `alert_others` | `false` | Also anger nearby creatures |

### `ancientcreature:territorial` <Badge type="info" text="priority 2" />
Attacks players who come close. The owner is always excluded.

| Option | Default | Notes |
| --- | --- | --- |
| `range` | `12.0` | Blocks |
| `random_interval` | `5` | Reciprocal chance to re-scan |
| `require_weapon` | `true` | Only players holding a weapon |
| `adults_only` | `false` | |
| `requires_hunger` | `false` | Only while hungry |

`requires_hunger` defaults to off because guarding your space is defence, and a creature that only did it
while starving would ignore threats it should answer.

Set it to `true` when you also set `require_weapon: false`. That combination is not defence — it is hunting
players on sight — and without the gate the creature charges anyone who walks into range, forever. The
shipped `apex_predator` and `flying_predator` profiles do exactly this.

### `ancientcreature:hunt_animals` <Badge type="info" text="priority 3" />
Preys on ordinary animals. Never the hunter's own species.

| Option | Default | Notes |
| --- | --- | --- |
| `random_interval` | `10` | |
| `adults_only` | `true` | |
| `requires_hunger` | `true` | Only while hungry |

Gated by default: eating animals is feeding. Set `requires_hunger: false` for a creature that should hunt
regardless.

### `ancientcreature:hunt_hostiles` <Badge type="info" text="priority 4" />
Attacks nearby hostile mobs.

| Option | Default | Notes |
| --- | --- | --- |
| `random_interval` | `5` | |
| `adults_only` | `false` | |
| `requires_hunger` | `false` | Only while hungry |

Not gated by default. Driving off a zombie is defence, not a meal — a herbivore uses this component too.

### `ancientcreature:aquatic_predator` <Badge type="info" text="priority 2" />
Hunts what is swimming nearby.

| Option | Default | Notes |
| --- | --- | --- |
| `random_interval` | `10` | |
| `target_players` | `true` | |
| `target_aquatic_life` | `true` | Squid, fish, dolphins, guardians |
| `require_in_water` | `true` | Ignore anything on land |
| `adults_only` | `true` | |
| `requires_hunger` | `true` | Only while hungry |

Gated by default, so a fed Megalodon lets swimmers past.

## Always true

Regardless of components, a creature never attacks:

* another creature of its **own species**
* its **owner** — the player who released it from a capsule or was nearby when its egg hatched

## Live list

```
/ancientcreature behaviors
```

prints every registered component type with its default priority, and every profile.
