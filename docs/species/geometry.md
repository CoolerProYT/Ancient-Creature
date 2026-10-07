# Bedrock geometry

`assets/<namespace>/ancientcreature/geo/<name>.geo.json`

A standard Blockbench **Bedrock Entity** export. No conversion step.

## Supported

* both file layouts Bedrock accepts: `1.12.0`+ (`"minecraft:geometry": [...]`) and the legacy `1.8.0` /
  `1.10.0` layout (`"geometry.name": {...}`), including `"geometry.child:geometry.parent"` inheritance
  within a file
* several geometries per file. A client entity refers to them by identifier (`geometry.triceratops`);
  the first geometry in a file can also be referred to by file id (`ancientcreature:triceratops`), which
  is how format-1 species files did it
* `description.identifier` (must start with `geometry.`), `texture_width`, `texture_height`
* bones: `name`, `parent`, `pivot`, `rotation`, `bind_pose_rotation`, `mirror`, `inflate`,
  `never_render` (or `neverRender`), `reset`, `locators`, `poly_mesh`
* cubes: `origin`, `size`, `inflate`, `mirror`, `pivot` + `rotation`, box `uv`, per-face `uv` with
  `uv_size` and `uv_rotation`
* `poly_mesh`: explicit polygons, or `"tri_list"` / `"quad_list"`
* locators, as `[x, y, z]` or `{ "offset": [...], "rotation": [...] }`. Animation sound and particle
  effects can play at them
* numbers written as strings, as Bedrock allows

Bone and locator names are matched case-insensitively, as in Bedrock.

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

## UV

Box UV, per-face UV and `uv_rotation` all go through one path and match Blockbench texel for texel. Box
UV is expanded to per-face rectangles the way Bedrock does it: cube sizes floored to whole texels, and
`mirror` flipping each face and swapping east and west.

Bedrock's `east` face is the cube's **negative-X** side and `west` the positive side. This is how
Blockbench reads and writes them, so it only matters if you write geometry by hand.

## Texture resolution

Textures are not restricted to 16×16. `texture_width` and `texture_height` may be any positive sizes
that match the PNG; 32, 64, 128, 256 and 512 are all practical choices. Higher resolution gives room
for gradients and mottling, but costs more memory. The shipped Pteranodon uses 128×128 and
Deinonychus and Ankylosaurus use 256×256, while the much larger Brachiosaurus UV layout uses 512×512.

## Rotated cubes

A cube with its own `pivot` and `rotation` is baked straight into its bone's vertices, rotated about its
own pivot. The baked model has exactly one part per bone, so animations address bones by their real names.

## Coordinate conversion

The baker uses the same mapping as Blockbench's Bedrock importer and Java exporter together, so a model
renders in game exactly as it does in Blockbench and in Bedrock:

| Quantity | Conversion |
| --- | --- |
| Bone offset | `(px - parent.px, -(py - parent.py), pz - parent.pz)`, plus 24 on Y for root bones |
| Bone and cube rotation | The file's `[x, y, z]` degrees, as written |
| Cube box | `(ox - px, py - oy - height, oz - pz)` relative to the bone pivot |

Animation keyframes are applied the way Blockbench's Java exporter applies them: rotation added as-is,
position as `(x, -y, z)`, scale multiplied.

::: tip Checked, not assumed
`BedrockGeometryTest` bakes a Bedrock cow and compares every vertex and UV against the vanilla Java cow,
and checks per-face UV against box UV, `uv_rotation`, rotated cubes, legacy inheritance and poly meshes.
:::

::: info Packs from earlier releases
Format-1 species files were authored for the earlier axis handling. Their geometry and animations are
converted on load so they look exactly as they did; `LegacyAxesTest` checks this against the old baker.
:::

## Not used

`visible_bounds_width`, `visible_bounds_height` and `visible_bounds_offset` are parsed but ignored —
culling uses the entity's own bounding box, which comes from the species' `physical` block.
