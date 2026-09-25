# Animation controllers

`assets/<namespace>/ancientcreature/animation_controllers/<name>.controller.json`

A small state machine deciding which animation plays. Optional — without one, a creature renders in its
rest pose.

```json
{
  "format_version": 1,
  "initial_state": "idle",
  "states": {
    "idle": {
      "animations": ["idle"],
      "blend_transition": 0.2,
      "transitions": [
        { "death": "query.is_dead" },
        { "graze": "query.action == 'graze'" },
        { "walk": "query.is_moving" }
      ]
    },
    "walk": {
      "animations": ["idle", "walk"],
      "blend_transition": 0.15,
      "transitions": [
        { "idle": "!query.is_moving" }
      ]
    },
    "graze": {
      "animations": ["graze"],
      "blend_transition": 0.3,
      "transitions": [
        { "idle": "query.action != 'graze'" }
      ]
    },
    "death": { "animations": ["idle"], "transitions": [] }
  }
}
```

## States

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `animations` | string[] | `[]` | Played together and stacked |
| `blend_transition` | float | `0.2` | Seconds to fade in. `0` is a hard cut |
| `transitions` | array | `[]` | Checked in order; the first true one wins |

`initial_state` defaults to `"idle"`.

Each transition is a single-entry object mapping the **target state** to its **condition**:

```json
{ "walk": "query.is_moving" }
```

A chain of immediately-true transitions settles in one frame, and cannot loop forever.

## Supported queries

| Query | Value |
| --- | --- |
| `query.anim_time` | Seconds in the current controller state |
| `query.life_time` | Seconds since the entity spawned |
| `query.head_yaw`, `query.head_pitch` | Degrees |
| `query.limb_swing`, `query.limb_swing_amount` | Walk animation position and speed |
| `query.ground_speed` | Alias of `limb_swing_amount` |
| `query.health`, `query.max_health` | Hit points |
| `query.is_moving` | 0 or 1 |
| `query.is_running` | Sprinting — set while charging |
| `query.is_attacking` | Has a target |
| `query.is_baby`, `query.is_in_water` | 0 or 1 |
| `query.is_hurt`, `query.is_dead` | 0 or 1 |
| `query.is_on_ground` | 0 or 1. A flying species needs this to tell perched from airborne |
| `query.hunger` | How fed the creature is. High is full, zero is starving |
| `query.is_hungry` | 0 or 1. Whether hunger has fallen to the species' `hunt_threshold` |
| `query.action` | `none`, `attack`, `roar`, `graze`, `eat` |

`q.` is accepted as a synonym for `query.`.

A flyer uses `is_on_ground` to choose between perched and airborne clips, and `is_hungry` is useful for a
leaner idle or a feeding pose:

```json
{ "idle": "query.is_on_ground && !query.is_moving" }
{ "fly": "!query.is_on_ground" }
{ "starving_idle": "query.is_hungry" }
{ "eat": "query.action == 'eat'" }
```

`hunger` is a number, so it can be compared: `query.hunger < 4` is a nearly starved creature.

Other mods can add queries — see [the API](#adding-your-own-queries).

## `query.action`

Continuous state (moving, sprinting, in water, baby, health) is derived client-side. `action` covers the
discrete things the client cannot infer: the server sets it when a bite lands, a roar starts, or a
graze begins, and clears it afterwards.

| Value | Set by |
| --- | --- |
| `attack` | Landing a melee hit — cleared after 8 ticks |
| `roar` | The `roar_attack` component, for its `roar_duration` |
| `graze` | The `graze` component, for its `duration` |
| `eat` | Reserved |
| `none` | Nothing in progress |

## Expression grammar

```
expression := or
or         := and ( "||" and )*
and        := comparison ( "&&" comparison )*
comparison := unary ( ("=="|"!="|">="|"<="|">"|"<") unary )?
unary      := "!" unary | primary
primary    := number | 'string' | "true" | "false" | "query."name | "(" expression ")"
```

Numbers are truthy when non-zero. String comparison supports `==` and `!=` only, which is what
`query.action` needs.

```
query.is_moving
!query.is_running
query.is_moving && !query.is_running
query.health < 10
query.action == 'graze'
(query.is_running || query.is_moving) && !query.is_baby
```

::: warning No Molang functions
Anything outside this grammar is a **load-time error** naming the offending text — `math.sin(...)`,
`variable.x`, `temp.y` and arithmetic are all rejected. You get a log line, not a silently dead
transition.
:::

Expressions are parsed once when the resource pack loads and evaluated as a compiled tree. Nothing is
parsed per frame.

## The server stays authoritative

Controllers are purely presentational. They read synchronised state and choose clips; they never apply
damage, never change entity state and never send anything to the server. The server decides when an
attack lands — the controller only draws the matching animation.

## Adding your own queries

```java
// during client initialization
AncientCreatureClientApi.registerNumberQuery("wing_beat",
    (state, secondsInState) -> Math.sin(state.ageInTicks * 0.3) * 0.5 + 0.5);

AncientCreatureClientApi.registerStringQuery("mood",
    (state, secondsInState) -> state.creatureHealth < 5 ? "afraid" : "calm");
```

```json
{ "glide": "query.wing_beat > 0.8" }
{ "cower": "query.mood == 'afraid'" }
```

Names that clash with a built-in query are rejected. Register before any resource pack loads.
