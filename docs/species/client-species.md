# Species (resource pack)

How a species **looks**. Never loaded by a dedicated server.

A species is drawn from up to three resource-pack files, all in standard Bedrock formats that Blockbench
exports, plus one small file for the things Bedrock has no field for:

| File | Location | Holds |
| --- | --- | --- |
| Client entity | `assets/<ns>/ancientcreature/entity/*.json` | Geometry, textures, materials, animations, scripts, render controllers |
| Render controller | `assets/<ns>/ancientcreature/render_controllers/*.json` | Which geometry, texture layers and bones to draw, chosen by Molang |
| Species settings | `assets/<ns>/ancientcreature/species/<name>.json` | Shadow, render scale, GUI framing |

Geometry, animations and animation controllers live under `ancientcreature/geo/`,
`ancientcreature/animations/` and `ancientcreature/animation_controllers/`. See
[Bedrock geometry](/species/geometry), [Bedrock animations](/species/animations) and
[Animation controllers](/species/animation-controllers).

## Client entity

A standard Bedrock `minecraft:client_entity`. Its `description.identifier` is the **species id** it
dresses. This is the shipped Triceratops:

```json
{
  "format_version": "1.10.0",
  "minecraft:client_entity": {
    "description": {
      "identifier": "ancientcreature:triceratops",
      "materials": { "default": "entity_alphatest" },
      "textures": {
        "default": "textures/entity/triceratops",
        "mottled": "textures/entity/triceratops_mottled",
        "saddle": "textures/entity/gear/triceratops_saddle",
        "armor_iron": "textures/entity/gear/triceratops_armor_iron",
        "armor_golden": "textures/entity/gear/triceratops_armor_golden",
        "armor_diamond": "textures/entity/gear/triceratops_armor_diamond",
        "armor_netherite": "textures/entity/gear/triceratops_armor_netherite"
      },
      "geometry": { "default": "geometry.triceratops" },
      "animations": {
        "idle": "animation.triceratops.idle",
        "walk": "animation.triceratops.walk",
        "charge": "animation.triceratops.charge",
        "graze": "animation.triceratops.graze",
        "bellow": "animation.triceratops.bellow",
        "main": "controller.animation.triceratops.main"
      },
      "scripts": { "animate": ["main"] },
      "render_controllers": ["controller.render.ancientcreature.triceratops"]
    }
  }
}
```

| Field | Notes |
| --- | --- |
| `identifier` | The species id, e.g. `mypack:dodo` |
| `textures` | Written the Bedrock way: relative to the pack root, without `.png`. Resolved in the file's own namespace unless the path names one (`othermod:textures/...`) |
| `geometry` | Short name → `geometry.*` identifier from a `.geo.json` |
| `materials` | Short name → material. Names containing `alphablend`, `translucent` or `blend` render translucent, names containing `emissive` render full-bright, everything else renders cutout (`entity_alphatest`) |
| `animations` | Short name → `animation.*` or `controller.animation.*` |
| `scripts.initialize` | Molang run once when the creature is first drawn, typically to set `variable.*` |
| `scripts.pre_animation` | Molang run every frame before animating |
| `scripts.animate` | Animations and controllers to run every frame: names, or `{ "name": "blend weight or condition" }` |
| `scripts.scale`, `scalex`/`scaley`/`scalez` | Molang render scale, on top of growth scale and `render_scale` |
| `render_controllers` | Names, or `{ "name": "condition" }` to apply a controller only while the condition holds |
| `sound_effects` | Short name → sound event id, for animation `sound_effects` tracks |
| `particle_effects` | Short name → particle id, for animation `particle_effects` tracks. Java particle ids that take no options (`minecraft:flame`, `minecraft:heart`, ...) work, plus the common Bedrock built-ins (`minecraft:basic_smoke_particle`, `heart_particle`, ...) |

All Molang is the [full Bedrock language](/species/animation-controllers#molang) and can read every
[query](/species/animation-controllers#queries).

::: tip Without a render controller
If a client entity lists no render controller (or none applies), the `default` geometry is drawn with the
texture named after the creature's variant, falling back to `default`. That is enough for a simple
species, but saddles and armor only show through a render controller.
:::

## Render controllers

A standard Bedrock render controller file. The shipped species all use the same shape: a skin chosen by
variant, then armor and saddle layers on top.

```json
{
  "format_version": "1.8.0",
  "render_controllers": {
    "controller.render.ancientcreature.triceratops": {
      "arrays": {
        "textures": {
          "Array.skins": ["Texture.default", "Texture.mottled"],
          "Array.armor": ["Texture.armor_iron", "Texture.armor_golden", "Texture.armor_diamond", "Texture.armor_netherite"]
        }
      },
      "geometry": "Geometry.default",
      "materials": [{ "*": "Material.default" }],
      "textures": [
        "Array.skins[query.variant]",
        "query.armor_tier > 0 ? Array.armor[query.armor_tier - 1] : 0",
        "query.is_saddled ? Texture.saddle : 0"
      ]
    }
  }
}
```

Supported: `arrays` (`textures`, `geometries`, `materials`), `geometry`, `textures` (several entries are
drawn as stacked layers; an entry that evaluates to `0` draws nothing), `materials`, `part_visibility`
(bone patterns with `*` wildcards, later rules winning), `color`, `overlay_color`, `is_hurt_color`,
`on_fire_color` and `ignore_lighting`.

Gear overlays are ordinary texture layers on the creature's own geometry, so a saddle or armor texture is
the same size as the skin and only paints the pixels the gear covers. `query.armor_tier` is `0` without
armor, then `1`–`4` for iron, golden, diamond and netherite.

## Species settings

`assets/<namespace>/ancientcreature/species/<name>.json`

```json
{
  "format_version": 2,
  "shadow_radius": 0.9,
  "render_scale": 1.0,
  "gui": {
    "scale": 12.72,
    "translation": [0.0, 0.0, 0.0],
    "rotation": [0.0, 215.0, 0.0]
  }
}
```

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `format_version` | int | `2` | `1` is the earlier format, see [below](#format-version-1) |
| `client_entity` | id | the species id | Which client entity to use, if its identifier differs from the species id |
| `shadow_radius` | float 0–16 | `0.5` | Multiplied by the creature's growth scale |
| `render_scale` | float | `1.0` | Visual only; does not change the hitbox |
| `gui` | object | see below | Framing in GUIs |

The whole file is optional: a client entity whose identifier is the species id is used with default
shadow and GUI settings.

If a species has neither, the log says so once, naming both places it looked.

### gui

Creatures differ enormously in proportion, so one hard-coded framing cannot suit them all. These values
place each one in the Fossil Identification Chamber's preview.

| Field | Type | Default | Notes |
| --- | --- | --- | --- |
| `scale` | float | `12.0` | Zoom. Bigger creatures want a smaller number |
| `translation` | `[x, y, z]` | `[0, 0, 0]` | Nudge inside the cell; `y` moves it up or down |
| `rotation` | `[x, y, z]` | `[0, 215, 0]` | Euler angles in degrees |

## Variants

Which variant a creature has is decided by the **server**, from the datapack's
[`variants`](/species/species-json#variants) list, and saved on the creature. The resource pack only
decides what each variant looks like:

* with a render controller, read `query.variant` (the variant's position in the datapack list, so
  `0` is the first) or `query.variant_name` (its id), as `Array.skins[query.variant]` does above;
* without one, give the client entity a texture named after each variant id.

## Format version 1

Packs written for earlier releases name the files directly:

```json
{
  "format_version": 1,
  "geometry": "mypack:dodo",
  "texture": "mypack:textures/entity/dodo.png",
  "animations": "mypack:dodo",
  "controller": "mypack:dodo",
  "shadow_radius": 0.5,
  "variants": [
    { "id": "default", "texture": "mypack:textures/entity/dodo.png" },
    { "id": "albino", "texture": "mypack:textures/entity/dodo_albino.png" }
  ]
}
```

These still load. They are converted on load to an equivalent client entity, and their geometry and
animations, which were authored for the earlier axis handling, are converted so the species looks exactly
as it did. Variant textures listed here are used for the variant ids the server rolls; the weights in this
file are ignored, since the server now rolls variants from the datapack. `geometry` and `texture` are
required in format 1.

New packs should use a client entity: it is what Blockbench exports, and it is the only way to get render
controllers, gear layers and scripts.
