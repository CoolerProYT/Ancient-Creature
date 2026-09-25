# Add a species

We will add a Stegosaurus to a pack called `mypack`. Nothing here needs a mod build or a Java compiler.

## 1. Model it

Open Blockbench and start a **Bedrock Entity** project. Build the model, then:

* keep **box UV** (Blockbench's per-face UV is only approximated — see [Geometry](/species/geometry#per-face-uv))
* name the bones something you will recognise; animations target them by name
* set the geometry identifier to `geometry.stegosaurus`

Export as **Bedrock Geometry** to:

```
assets/mypack/ancientcreature/geo/stegosaurus.geo.json
```

Then animate it and export as **Bedrock Animation** to:

```
assets/mypack/ancientcreature/animations/stegosaurus.animation.json
```

Name the animations `animation.stegosaurus.idle`, `animation.stegosaurus.walk`, and so on. The
controller can refer to them by the short name (`idle`, `walk`).

## 2. Gameplay

`data/mypack/ancientcreature/species/stegosaurus.json`

```json
{
  "format_version": 1,
  "entity_category": "land",
  "physical": {
    "width": 1.6,
    "height": 2.0,
    "eye_height": 1.5
  },
  "attributes": {
    "max_health": 60.0,
    "movement_speed": 0.2,
    "attack_damage": 10.0,
    "attack_knockback": 1.8,
    "follow_range": 24.0,
    "knockback_resistance": 0.7
  },
  "growth": {
    "adult_age": 24000,
    "baby_scale": 0.45
  },
  "behavior": {
    "profile": "ancientcreature:defensive_herbivore"
  },
  "diet": {
    "items": ["minecraft:fern", "minecraft:large_fern"]
  },
  "spawn": {
    "biomes": "#minecraft:is_forest",
    "incubation_time": 1400
  },
  "sounds": {
    "ambient": "minecraft:entity.ravager.ambient",
    "hurt": "minecraft:entity.ravager.hurt",
    "death": "minecraft:entity.ravager.death",
    "step": "minecraft:entity.ravager.step",
    "ambient_interval": 260
  }
}
```

Full field list: [Species JSON](/species/species-json).

## 3. Appearance

`assets/mypack/ancientcreature/species/stegosaurus.json`

```json
{
  "format_version": 1,
  "geometry": "mypack:stegosaurus",
  "texture": "mypack:textures/entity/stegosaurus.png",
  "animations": "mypack:stegosaurus",
  "controller": "mypack:stegosaurus",
  "shadow_radius": 1.0,
  "gui": {
    "scale": 11.0,
    "rotation": [0.0, 215.0, 0.0]
  }
}
```

::: tip
`geometry`, `animations` and `controller` are *file* ids, not the `geometry.stegosaurus` identifier
inside the model. `mypack:stegosaurus` means the file
`assets/mypack/ancientcreature/geo/stegosaurus.geo.json`.
:::

Drop the texture at `assets/mypack/textures/entity/stegosaurus.png`.

The texture does not have to be 16×16. Set the Blockbench project's texture resolution to the PNG's
real dimensions before export; the resulting `texture_width` and `texture_height` are read directly.

## 4. Decide when animations play

`assets/mypack/ancientcreature/animation_controllers/stegosaurus.controller.json`

```json
{
  "format_version": 1,
  "initial_state": "idle",
  "states": {
    "idle": {
      "animations": ["idle"],
      "blend_transition": 0.2,
      "transitions": [
        { "attack": "query.action == 'attack'" },
        { "walk": "query.is_moving" }
      ]
    },
    "walk": {
      "animations": ["walk"],
      "blend_transition": 0.15,
      "transitions": [
        { "attack": "query.action == 'attack'" },
        { "idle": "!query.is_moving" }
      ]
    },
    "attack": {
      "animations": ["attack"],
      "blend_transition": 0.08,
      "transitions": [
        { "walk": "query.action != 'attack' && query.is_moving" },
        { "idle": "query.action != 'attack'" }
      ]
    }
  }
}
```

The controller is optional. Without one the creature renders in its rest pose, which is a perfectly
good way to check the model before animating it.

## 5. Name it

Add to your language file, `assets/mypack/lang/en_us.json`:

```json
{
  "species.mypack.stegosaurus": "Stegosaurus"
}
```

The translation key follows the species id: `species.<namespace>.<path>`.

## 6. Load it

Put the datapack half in the world's `datapacks/` folder (or a datapack that is already enabled), the
resource pack half in `resourcepacks/`, and enable both.

```
/reload
/ancientcreature species list
```

You should see `mypack:stegosaurus` listed. Then:

```
/ancientcreature summon mypack:stegosaurus
```

If something is wrong, the log says what and where. Keep going with
[Testing a species](/species/testing).
