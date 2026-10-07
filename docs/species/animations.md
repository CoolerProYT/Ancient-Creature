# Bedrock animations

`assets/<namespace>/ancientcreature/animations/<name>.animation.json`

A standard Blockbench **Bedrock Animation** export.

```json
{
  "format_version": "1.8.0",
  "animations": {
    "animation.triceratops.idle": {
      "animation_length": 4.0,
      "loop": true,
      "bones": {
        "body": {
          "rotation": {
            "0.0": [0, 0, 0],
            "1.0": { "post": [-0.8, 0, 0], "lerp_mode": "catmullrom" },
            "4.0": [0, 0, 0]
          },
          "position": { "0.0": [0, 0, 0], "2.0": [0, 0.32, 0] }
        }
      }
    }
  }
}
```

## Naming

Name animations `animation.<species>.<state>`. The client entity's `animations` map gives each one a short
name, and controllers and `scripts.animate` use the short name:

```json
"animations": { "idle": "animation.triceratops.idle", "main": "controller.animation.triceratops.main" }
```

## Supported

| Feature | Notes |
| --- | --- |
| `animation_length` | Falls back to the last keyframe's time if omitted |
| `start_delay`, `loop_delay` | Molang, in seconds |
| `anim_time_update` | Molang deciding how `query.anim_time` advances, e.g. to play at walking speed |
| `blend_weight` | Molang weight for the whole animation |
| `override_previous_animation` | Replaces, rather than adds to, the pose of earlier animations |
| `loop: true` | Wraps forever |
| `loop: false` | Plays once, then stops contributing |
| `loop: "hold_on_last_frame"` | Plays once and holds the final pose |
| `bones.<name>.rotation` | Degrees |
| `bones.<name>.position` | Model units |
| `bones.<name>.scale` | Multiplier around 1 |
| Scalar value | `"0.0": 5` applies 5 to all three axes |
| Vector value | `"0.0": [0, 5, 0]` |
| Molang values | `"0.0": ["math.sin(query.anim_time * 360) * 5", 0, 0]`, evaluated every frame |
| `{ "pre": …, "post": … }` | Different value approaching and leaving the keyframe — a step |
| `lerp_mode: "linear"` | Default |
| `lerp_mode: "catmullrom"` | Smooth spline through neighbouring keyframes |
| `lerp_mode: "step"` | Holds the value until the next keyframe |
| Constant channel | A channel written as one value instead of a keyframe map |
| `relative_to: { "rotation": "entity" }` | Rotation authored in entity space rather than the parent bone's |
| `sound_effects` | `{ "time": { "effect": "name", "locator": "..." } }`. Names map through the client entity's `sound_effects` |
| `particle_effects` | `{ "time": { "effect": "name", "locator": "...", "pre_effect_script": "..." } }`. Names map through the client entity's `particle_effects` |
| `timeline` | `{ "time": "molang" }` or a list of statements, run as playback passes each time |

Animations are **additive deltas over the model's rest pose**, matching how Minecraft's own keyframe
animations work. Multiple animations in one controller state stack.

Keyframes are sampled the way Blockbench's animator samples them, so playback in game matches its preview.
Nothing is parsed while rendering.

## Blending

When a controller changes state, the outgoing animation fades out while the incoming one fades in over
the state's `blend_transition` seconds. Leave it out (or `0`) for a hard cut.

## Not supported

A keyframe with a non-numeric timestamp is skipped with a warning rather than failing the whole file.

Molang that does not compile is a load-time error naming the animation. See
[Molang](/species/animation-controllers#molang) for what the language supports.

## Missing bones

If an animation targets a bone the geometry does not have, that channel is ignored and a warning is
logged once naming the animation and the bone. The rest of the animation still plays — a renamed bone
will not black-hole the whole creature.
