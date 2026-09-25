# Bedrock geometry

`assets/<namespace>/ancientcreature/geo/<name>.geo.json`

A standard Blockbench **Bedrock Entity** export. No conversion step.

## Supported

* `format_version` and the `minecraft:geometry` list
* `description.identifier` (must start with `geometry.`), `texture_width`, `texture_height`
* several geometries in one file — the first takes the file's id, the rest are addressed by their own
  identifier with dots turned into slashes
* bones: `name`, `parent`, `pivot`, `rotation`, `neverRender`
* cubes: `origin`, `size`, `uv` (box form), `inflate`, `mirror`
* per-cube `pivot` + `rotation`

Nothing is parsed per frame. Geometry is read and baked once per resource reload and cached; the cache
is dropped wholesale on the next reload, so no stale model survives an F3+T.

## Validation

Rejected at load, naming the file and every problem it found:

| Problem | Example |
| --- | --- |
| Duplicate bone names | two bones called `head` |
| Unknown parent | `"parent": "torso"` with no `torso` bone |
| Parent cycle | `a` parents `b`, `b` parents `a` |
| Negative or non-finite cube size | `"size": [-1, 1, 1]` |
| Non-finite cube origin | `NaN` from a broken export |
| Malformed pivot | non-finite values |
| Bad identifier | `"identifier": "my_model"` — must start with `geometry.` |
| Non-positive texture size | `"texture_width": 0` |

## Per-face UV

::: warning Approximated, not reproduced
Minecraft's cube builder has no per-face UV entry point. A cube using the per-face form is accepted,
but its UV is approximated from the smallest face corner and a warning is logged naming the geometry
and bone. The texture will very likely be wrong.

**Export with box UV.** In Blockbench: *File → Project*, set UV mode to **Box UV**.
:::

## Texture resolution

Textures are not restricted to 16×16. `texture_width` and `texture_height` may be any positive sizes
that match the PNG; 32, 64, 128, 256 and 512 are all practical choices. Higher resolution gives room
for gradients and mottling, but costs more memory. The shipped Pteranodon uses 128×128 and
Deinonychus and Ankylosaurus use 256×256, while the much larger Brachiosaurus UV layout uses 512×512.

## Rotated cubes

Minecraft models rotate *bones*, not individual cubes. A cube with its own `rotation` is turned into a
child bone named `<bone>_r1`, `<bone>_r2`, and so on — exactly the trick Blockbench itself uses when it
exports a Java model. This means a model that originally came from a Java export round-trips exactly.

## Coordinate conversion

Bedrock and Java model space are related by the rigid map:

```
M(x, y, z) = (-x, 24 - y, z)
```

a 180° rotation about Z plus a translation putting the origin at the model's feet. Under it:

| Quantity | Conversion |
| --- | --- |
| Bone pivot | `(-px, 24 - py, pz)` |
| Cube corner | `(-(ox + w), 24 - (oy + h), oz)` |
| Rotation (degrees) | `(-rx, -ry, rz)` |

The loader implements exactly this, so a model converted from a hand-written Java `LayerDefinition`
renders identically.

::: tip This is checked, not assumed
`BedrockModelBakerTest` bakes each shipped `.geo.json` and walks it with `ModelPart#visit`, comparing
every cube's fully composed rest-pose vertices and UVs against the original Java model. All three
migrated creatures match exactly. Tyrannosaurus Rex is the interesting case: it has bones that are both
rotated *and* parents, which is where a naive conversion breaks.
:::

## Not used

`visible_bounds_width`, `visible_bounds_height` and `visible_bounds_offset` are parsed but ignored —
culling uses the entity's own bounding box, which comes from the species' `physical` block.
