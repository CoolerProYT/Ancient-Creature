# Minimal example

The smallest species that actually works. Everything not listed takes its default.

Namespace is `examplepack` throughout — replace it with yours.

## The absolute minimum

Two files and a texture. No animations, no controller: the creature renders in its rest pose, which is
a fine way to check a model before animating it.

### `data/examplepack/ancientcreature/species/dodo.json`

```json
{
  "physical": { "width": 0.6, "height": 0.9 },
  "attributes": { "max_health": 8.0, "movement_speed": 0.25 },
  "behavior": { "profile": "ancientcreature:passive" },
  "diet": { "items": ["minecraft:wheat_seeds"] }
}
```

### `assets/examplepack/ancientcreature/species/dodo.json`

```json
{
  "geometry": "examplepack:dodo",
  "texture": "examplepack:textures/entity/dodo.png"
}
```

### `assets/examplepack/ancientcreature/geo/dodo.geo.json`

```json
{
  "format_version": "1.12.0",
  "minecraft:geometry": [
    {
      "description": {
        "identifier": "geometry.dodo",
        "texture_width": 32,
        "texture_height": 32
      },
      "bones": [
        { "name": "root", "pivot": [0, 0, 0] },
        {
          "name": "body",
          "parent": "root",
          "pivot": [0, 8, 0],
          "cubes": [
            { "origin": [-4, 4, -4], "size": [8, 8, 8], "uv": [0, 0] }
          ]
        },
        {
          "name": "head",
          "parent": "body",
          "pivot": [0, 12, -3],
          "cubes": [
            { "origin": [-2, 12, -6], "size": [4, 4, 4], "uv": [0, 16] }
          ]
        }
      ]
    }
  ]
}
```

### `assets/examplepack/lang/en_us.json`

```json
{
  "species.examplepack.dodo": "Dodo"
}
```

Plus a 32×32 texture at `assets/examplepack/textures/entity/dodo.png`.

```
/reload
/ancientcreature summon examplepack:dodo
```

## Adding animation

### `assets/examplepack/ancientcreature/animations/dodo.animation.json`

```json
{
  "format_version": "1.8.0",
  "animations": {
    "animation.dodo.idle": {
      "animation_length": 2.0,
      "loop": true,
      "bones": {
        "head": {
          "rotation": {
            "0.0": [0, 0, 0],
            "1.0": [0, 12, 0],
            "2.0": [0, 0, 0]
          }
        }
      }
    },
    "animation.dodo.walk": {
      "animation_length": 0.8,
      "loop": true,
      "bones": {
        "body": {
          "rotation": {
            "0.0": [0, 0, -3],
            "0.4": [0, 0, 3],
            "0.8": [0, 0, -3]
          }
        }
      }
    }
  }
}
```

### `assets/examplepack/ancientcreature/animation_controllers/dodo.controller.json`

```json
{
  "format_version": 1,
  "initial_state": "idle",
  "states": {
    "idle": {
      "animations": ["idle"],
      "blend_transition": 0.2,
      "transitions": [{ "walk": "query.is_moving" }]
    },
    "walk": {
      "animations": ["walk"],
      "blend_transition": 0.15,
      "transitions": [{ "idle": "!query.is_moving" }]
    }
  }
}
```

Then point the client definition at them:

```json
{
  "geometry": "examplepack:dodo",
  "texture": "examplepack:textures/entity/dodo.png",
  "animations": "examplepack:dodo",
  "controller": "examplepack:dodo",
  "shadow_radius": 0.4,
  "gui": { "scale": 24.0, "rotation": [0.0, 215.0, 0.0] }
}
```

## A flying example

Only the differences from the above.

```json
{
  "entity_category": "flying",
  "physical": { "width": 0.8, "height": 0.7 },
  "attributes": {
    "max_health": 12.0,
    "movement_speed": 0.3,
    "flying_speed": 0.6,
    "follow_range": 32.0
  },
  "growth": { "baby_scale": 0.4 },
  "behavior": {
    "profile": "ancientcreature:flying_passive",
    "components": [
      { "type": "ancientcreature:fly", "min_altitude": 8, "max_altitude": 48 }
    ]
  },
  "diet": { "items": ["minecraft:cod"] }
}
```

`entity_category: "flying"` is what activates flight navigation — without it the flight components skip
themselves and you get a creature that walks.

The `fly` override is worth adding. Its default band is 5–32 blocks above the terrain, which keeps a flyer
fairly low; raise `max_altitude` for something that should soar. See
[`ancientcreature:fly`](/species/behaviors#ancientcreature-fly).

## An aquatic example

```json
{
  "entity_category": "aquatic",
  "physical": { "width": 1.4, "height": 0.9 },
  "attributes": {
    "max_health": 30.0,
    "movement_speed": 1.0,
    "attack_damage": 6.0,
    "follow_range": 24.0
  },
  "behavior": { "profile": "ancientcreature:aquatic_predator" },
  "spawn": { "biomes": "#minecraft:is_ocean", "incubation_time": 1800 }
}
```

Note there is no `diet` — the aquatic predator profile has no breeding or tempting, matching Megalodon.

It will only hunt when hungry. That is the default for every predator profile; a fed one lets swimmers
past. To make it hunt relentlessly instead:

```json
"behavior": {
  "profile": "ancientcreature:aquatic_predator",
  "components": [
    { "type": "ancientcreature:aquatic_predator", "requires_hunger": false }
  ]
}
```

See [hunger](/species/species-json#hunger).

## Shipped examples

Four complete, working species live in the mod itself:

```
common/src/main/resources/data/ancientcreature/ancientcreature/species/
common/src/main/resources/assets/ancientcreature/ancientcreature/
```

Triceratops (land, defensive), Tyrannosaurus Rex (land, apex predator), Megalodon (aquatic) and Pteranodon
(flying) — worth reading alongside this page.

The Pteranodon is the most useful complete example: it was built entirely on this pipeline and shows
flying behavior, altitude configuration, a grounded/airborne controller, six custom sounds and a
standard Blockbench Bedrock model.