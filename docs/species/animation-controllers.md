# Animation controllers

`assets/<namespace>/ancientcreature/animation_controllers/<name>.controller.json`

A state machine deciding which animations play, in the standard Bedrock format Blockbench exports. A
client entity runs it by listing it in `scripts.animate`. Without any, a creature renders in its rest
pose.

```json
{
  "format_version": "1.10.0",
  "animation_controllers": {
    "controller.animation.triceratops.main": {
      "initial_state": "idle",
      "states": {
        "idle": {
          "animations": ["idle"],
          "transitions": [
            { "death": "query.is_dead" },
            { "graze": "query.action == 'graze'" },
            { "walk": "query.is_moving" }
          ],
          "blend_transition": 0.2
        },
        "walk": {
          "animations": ["idle", { "walk": "math.clamp(query.modified_move_speed * 4, 0, 1)" }],
          "transitions": [{ "idle": "!query.is_moving" }],
          "blend_transition": 0.15
        },
        "graze": {
          "animations": ["graze"],
          "transitions": [{ "idle": "query.action != 'graze'" }],
          "blend_transition": 0.3
        },
        "death": { "animations": ["idle"] }
      }
    }
  }
}
```

One file can hold several controllers. Each is referenced from the client entity's `animations` map by
its full `controller.animation.*` name.

## States

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `animations` | array | `[]` | Short names from the client entity, or `{ "name": "blend weight" }` with a Molang weight |
| `transitions` | array | `[]` | Checked in order; the first true one wins |
| `blend_transition` | float or curve | `0` | Seconds to cross-fade into the next state, or a `{ "time": weight }` curve |
| `blend_via_shortest_path` | bool | `false` | Blend rotations the short way round |
| `on_entry`, `on_exit` | Molang[] | `[]` | Run when the state is entered or left, typically to set `variable.*` |
| `sound_effects` | array | `[]` | `{ "effect": "name" }`, played on entry. Names map through the client entity's `sound_effects` |
| `particle_effects` | array | `[]` | `{ "effect": "name", "locator": "bone_locator", "pre_effect_script": "..." }`, spawned on entry |

`initial_state` defaults to `"default"`.

Each transition is a single-entry object mapping the **target state** to its **condition**:

```json
{ "walk": "query.is_moving" }
```

A chain of immediately-true transitions settles in one frame, and cannot loop forever. An
`initial_state` or transition target that isn't a declared state is reported when the pack loads.

## Molang

Every condition, blend weight, keyframe and script is Molang, Bedrock's expression language, with the
whole language available:

* numbers, `'strings'`, `true`/`false`
* `query.`/`q.`, `variable.`/`v.` (per creature, kept between frames), `temp.`/`t.` (cleared every
  evaluation) and `context.`/`c.`
* arithmetic, comparisons, `&&`, `||`, `!`, `a ? b : c`, `a ? b`, `??`
* assignment, `;`-separated statements, `return`, `{}` blocks, `loop`, `for_each`, `break`, `continue`
* `Array.`, `Geometry.`, `Texture.` and `Material.` references in render controllers
* the `math.*` library, in degrees: `abs`, `sin`, `cos`, `asin`, `acos`, `atan`, `atan2`, `sqrt`,
  `pow`, `exp`, `ln`, `floor`, `ceil`, `round`, `trunc`, `mod`, `min`, `max`, `clamp`, `sign`,
  `copy_sign`, `lerp`, `lerprotate`, `inverse_lerp`, `hermite_blend`, `min_angle`, `random`,
  `random_integer`, `die_roll`, `die_roll_integer`, `pi`, and Blockbench's easing family
  (`math.ease_in_quad`, `math.ease_out_bounce`, `math.ease_in_out_elastic`, ...)

Everything is case-insensitive. The `->` entity-reference operator parses but always gives 0, since there
are no entity references outside Bedrock.

Expressions are compiled once when the resource pack loads; constant ones (most keyframes) are folded to a
number. Anything that fails to compile is a load-time error naming the file and the offending text. An
unknown query evaluates to 0 and is logged once, so a typo is visible rather than silently dead.

## Queries

Bedrock's own queries mean what they mean in Bedrock, so animations written for Bedrock entities work
unchanged.

| Query | Value |
| --- | --- |
| `anim_time` | Seconds the current animation or controller state has been running |
| `life_time` | Seconds since the creature spawned |
| `delta_time`, `frame_alpha` | Seconds since the last frame; partial tick |
| `time_of_day`, `moon_phase`, `time_stamp` | World time |
| `modified_distance_moved`, `modified_move_speed` | Walk cycle position and speed, for walk animations. `limb_swing` and `limb_swing_amount` are aliases |
| `ground_speed`, `vertical_speed` | Blocks per second |
| `walk_distance`, `yaw_speed` | |
| `body_y_rotation` | Degrees |
| `head_y_rotation`, `head_x_rotation` | Head yaw and pitch in degrees. Also `target_y_rotation`/`target_x_rotation` and `head_yaw`/`head_pitch` |
| `position(axis)`, `position_delta(axis)` | `0` = x, `1` = y, `2` = z |
| `distance_from_camera`, `scale` | |
| `health`, `max_health`, `hurt_time` | |
| `is_moving`, `is_on_ground`, `is_in_water`, `is_in_water_or_rain`, `is_in_lava`, `is_swimming`, `is_jumping` | 0 or 1 |
| `is_sprinting` | Set while charging. Also `is_running` |
| `is_gliding` | A flyer in the air. Also `is_flying` |
| `is_alive`, `is_dead`, `is_hurt`, `is_on_fire`, `is_baby`, `is_invisible`, `is_leashed` | 0 or 1 |
| `has_target` | Has an attack target. Also `is_attacking`, `is_angry` |
| `is_eating` | Grazing or eating. Also `is_grazing` |
| `is_roaring` | |
| `is_riding`, `has_rider` | |
| `is_tamed` | Has an owner |
| `is_saddled`, `is_sitting` | Saddled; told to stay with the Horn Whistle |
| `armor_tier` | `0` none, `1` iron, `2` golden, `3` diamond, `4` netherite |
| `hunger`, `is_hungry` | How fed the creature is (high is full); whether it has fallen to the species' `hunt_threshold` |
| `comfort`, `is_distressed` | Comfort 0–100; whether it is below 30 |
| `variant` | Position of the creature's variant in the datapack `variants` list. Also `mark_variant`, `skin_id` |
| `variant_name` | The variant id, as a string |
| `species` | The species id, as a string |
| `action` | `'none'`, `'attack'`, `'roar'`, `'graze'` or `'eat'`, see below |
| `all_animations_finished`, `any_animation_finished` | For the animations of the current controller state |
| `is_first_person` | Always 0 |

`q.` is accepted as a synonym for `query.`. Other mods can add queries — see
[the API](#adding-your-own-queries).

A flyer uses `is_on_ground` to choose between perched and airborne clips, and `is_hungry` is useful for a
leaner idle or a feeding pose:

```json
{ "idle": "query.is_on_ground && !query.is_moving" }
{ "fly": "!query.is_on_ground" }
{ "starving_idle": "query.is_hungry" }
{ "eat": "query.action == 'eat'" }
```

## `query.action`

Continuous state (moving, sprinting, in water, baby, health) is derived client-side. `action` covers the
discrete things the client cannot infer: the server sets it when a bite lands, a roar starts, or a
graze begins, and clears it afterwards.

| Value | Set by |
| --- | --- |
| `attack` | Landing a melee hit (cleared after 8 ticks), and most [mount abilities](/species/species-json#riding) |
| `roar` | The `roar_attack` component, for its `roar_duration`, and the `roar` mount ability |
| `graze` | The `graze` component, for its `duration` |
| `eat` | Reserved |
| `none` | Nothing in progress |

## The server stays authoritative

Controllers are purely presentational. They read synchronised state and choose clips; they never apply
damage, never change entity state and never send anything to the server. `variable.*` lives only on the
client that is drawing the creature. The server decides when an attack lands — the controller only draws
the matching animation.

## Format version 1

Controller files written for earlier releases (`"format_version": 1`, with `initial_state` and `states`
at the top level) still load as a single controller. They keep their old behaviour of blending for 0.2
seconds when a state sets no `blend_transition`, and of starting in `idle`.

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
