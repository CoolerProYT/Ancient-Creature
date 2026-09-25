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

Name animations `animation.<species>.<state>`. Controllers can refer to them by the full name or just
the short state name (`idle`, `walk`, `bite`), so either works:

```json
"animations": ["idle"]
"animations": ["animation.triceratops.idle"]
```

## Supported

| Feature | Notes |
| --- | --- |
| `animation_length` | Falls back to the last keyframe's time if omitted |
| `loop: true` | Wraps forever |
| `loop: false` | Plays once, then stops contributing |
| `loop: "hold_on_last_frame"` | Plays once and holds the final pose |
| `bones.<name>.rotation` | Degrees |
| `bones.<name>.position` | Model units |
| `bones.<name>.scale` | Multiplier around 1 |
| Scalar value | `"0.0": 5` applies 5 to all three axes |
| Vector value | `"0.0": [0, 5, 0]` |
| `{ "pre": …, "post": … }` | Different value approaching and leaving the keyframe — a step |
| `lerp_mode: "linear"` | Default |
| `lerp_mode: "catmullrom"` | Smooth spline through neighbouring keyframes |
| Constant channel | A channel written as one value instead of a keyframe map |

Animations are **additive deltas over the model's rest pose**, matching how Minecraft's own keyframe
animations work. Multiple animations in one controller state stack.

Sampling is a binary search with no per-frame allocation. Nothing is parsed while rendering.

## Blending

When a controller changes state, the outgoing animation fades out while the incoming one fades in over
the new state's `blend_transition` seconds. Set it to `0` for a hard cut.

## Not supported

::: warning No Molang
There is no expression evaluator for keyframe values. They must be literal numbers or vectors.
`math.sin(query.anim_time)` in a keyframe will not work.

Molang-style conditions *are* supported in animation **controllers**, in a documented subset — see
[Animation controllers](/species/animation-controllers#expression-grammar).
:::

A keyframe with a non-numeric timestamp is skipped with a warning rather than failing the whole file.

## Missing bones

If an animation targets a bone the geometry does not have, that channel is ignored and a warning is
logged once naming the animation and the bone. The rest of the animation still plays — a renamed bone
will not black-hole the whole creature.
