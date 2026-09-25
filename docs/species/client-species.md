# Species JSON (resource pack)

`assets/<namespace>/ancientcreature/species/<name>.json`

How a species **looks**. Never loaded by a dedicated server.

## Complete example

```json
{
  "format_version": 1,
  "geometry": "ancientcreature:triceratops",
  "texture": "ancientcreature:textures/entity/triceratops.png",
  "animations": "ancientcreature:triceratops",
  "controller": "ancientcreature:triceratops",
  "shadow_radius": 0.9,
  "render_scale": 1.0,
  "gui": {
    "scale": 12.0,
    "translation": [0.0, 0.0, 0.0],
    "rotation": [0.0, 215.0, 0.0]
  },
  "variants": [
    { "id": "default", "weight": 80, "texture": "ancientcreature:textures/entity/triceratops.png" },
    { "id": "dark",    "weight": 19, "texture": "ancientcreature:textures/entity/triceratops_dark.png" },
    { "id": "albino",  "weight": 1,  "texture": "ancientcreature:textures/entity/triceratops_albino.png" }
  ]
}
```

## Fields

| Field | Type | Required | Default | Notes |
| --- | --- | --- | --- | --- |
| `format_version` | int | no | `1` | |
| `geometry` | id | **yes** | — | File id under `ancientcreature/geo/` |
| `texture` | id | **yes** | — | Full texture path, e.g. `ns:textures/entity/x.png` |
| `animations` | id | no | none | File id under `ancientcreature/animations/` |
| `controller` | id | no | none | File id under `ancientcreature/animation_controllers/` |
| `shadow_radius` | float | no | `0.5` | Multiplied by the creature's growth scale |
| `render_scale` | float | no | `1.0` | Visual only; does not change the hitbox |
| `gui` | object | no | see below | Framing in the identification screen |
| `variants` | array | no | `[]` | Weighted texture variants |

::: tip File ids, not geometry identifiers
`geometry: "ancientcreature:triceratops"` points at the *file*
`assets/ancientcreature/ancientcreature/geo/triceratops.geo.json`. The `geometry.triceratops`
identifier inside that file is separate and is not what you write here.
:::

Referencing a geometry, animation file or controller that does not exist is reported as an error at
resource-load time, naming the species and the missing file.

## gui

Creatures differ enormously in proportion, so one hard-coded framing cannot suit them all. These
values place each one in the Fossil Identification Chamber's preview.

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `scale` | float | `12.0` | Zoom. Bigger creatures want a smaller number |
| `translation` | `[x, y, z]` | `[0, 0, 0]` | Nudge inside the cell; `y` moves it up or down |
| `rotation` | `[x, y, z]` | `[0, 215, 0]` | Euler angles in degrees |

For reference, the shipped values are `12.0` for Triceratops, `11.0` for Deinonychus, `10.0` for
Megalodon and Ankylosaurus, and `9.0` for Pteranodon and the much taller Tyrannosaurus Rex.

## variants

```json
"variants": [
  { "id": "default", "weight": 80, "texture": "ns:textures/entity/x.png" },
  { "id": "albino",  "weight": 1,  "texture": "ns:textures/entity/x_albino.png" }
]
```

| Field | Type | Required | Default |
| --- | --- | --- | --- |
| `id` | string | **yes** | — |
| `weight` | int | no | `1` |
| `texture` | id | **yes** | — |

Each creature stores its variant id and keeps it across saves and reloads — it is never re-rolled. If
the id disappears from the resource pack the default texture is used **without** clearing the saved id,
so restoring the pack restores the look.

::: warning Not rolled automatically yet
Variants are parsed, stored, synchronised, persisted and used when picking the texture, but nothing
currently assigns one at spawn. Every creature uses `default` unless a variant is set explicitly. See
[Limitations](/species/limitations).
:::
