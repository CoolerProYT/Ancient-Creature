# Custom species

<script setup>
import { data } from '../.vitepress/theme/ancientcreature'
</script>

Every creature in Ancient Creature, including all {{ data.species.length }} that ship with the mod, runs on one data-driven species system. A datapack and a resource pack can add a new one. There is no Java involved.

A species is identified by a resource id, like `ancientcreature:triceratops` or `mypack:stegosaurus`, and defined by five files:

```
data/<namespace>/ancientcreature/species/<name>.json                     gameplay
assets/<namespace>/ancientcreature/species/<name>.json                   appearance
assets/<namespace>/ancientcreature/geo/<name>.geo.json                   model
assets/<namespace>/ancientcreature/animations/<name>.animation.json      animations
assets/<namespace>/ancientcreature/animation_controllers/<name>.controller.json   when to play what
```

Five files. That is a creature.


- **One entity, any creature.** Every species runs on a single generic entity. Size, attributes, AI, sounds and growth all come from JSON.
- **Blockbench in, creature out.** Export a Bedrock model and animation straight from Blockbench. Geometry and animations load from the resource pack and reload with F3+T.
- **Safe by construction.** JSON can only pick from a fixed set of behaviour components. A broken file is reported with the reason and skipped; it never takes the world down.
- **Reloadable.** Edit a species, run `/reload`, and loaded creatures pick up new attributes, size and AI in place.


A new species joins the whole game loop: its fossils drop in the biomes you list, it gets its own DNA, genome and egg, and it shows up in JEI and the Species Journal.

::: warning Beta
The species format is still in beta. `format_version` is validated on load, so a future format change will tell you rather than silently misreading your files, but expect changes.
:::

## Where things live

| Concern | Location | Reloads with |
| --- | --- | --- |
| Gameplay: size, attributes, AI, sounds, growth, diet, hunger, fossil biome | `data/<ns>/ancientcreature/species/<name>.json` | `/reload` |
| Appearance: geometry, texture, variants, GUI framing | `assets/<ns>/ancientcreature/species/<name>.json` | F3+T |
| Model | `assets/<ns>/ancientcreature/geo/<name>.geo.json` | F3+T |
| Animations | `assets/<ns>/ancientcreature/animations/<name>.animation.json` | F3+T |
| Animation controller | `assets/<ns>/ancientcreature/animation_controllers/<name>.controller.json` | F3+T |

The file's path under `species/` **is** the id. A file at
`data/mypack/ancientcreature/species/stegosaurus.json` defines `mypack:stegosaurus`.

## Why gameplay and appearance are separate

Gameplay data lives under `data/` and appearance under `assets/` so that:

* a **dedicated server** never loads a model, texture or animation — it only needs the `data/` half;
* a **resource pack** can restyle a creature without touching balance;
* a **datapack** can rebalance a creature without shipping any art.

The server sends its species definitions to each client on join and after every reload, because the
client needs the physical size and growth to size the entity locally. Models and textures are never
sent through packets — they come from the resource pack like any other asset.

## One entity type

Every species spawns as `ancientcreature:ancient_creature`. It swaps its navigation and movement
control when the species is applied, so land, aquatic and flying creatures all share the single type.

The three ids from before the migration — `ancientcreature:triceratops`, `:tyrannosaurus_rex` and
`:megalodon` — remain as compatibility registrations so creatures already placed in older worlds are
not lost. New creatures, including Pteranodon and pack-added species, use the single generic entity.
See [Migration](/species/migration).

## Predators hunt when hungry

Every creature has a [hunger](/species/species-json#hunger) meter. Predators only pick prey once it falls far
enough, and stop once they have eaten, so they do not attack everything that walks past.

This gates prey *selection* only. Fighting back, driving off hostile mobs and the attack goals themselves
are never gated, so a well-fed creature still defends itself.

## What a pack cannot do

Behaviour is chosen from a [closed set of components](/species/behaviors). A pack picks and configures
them; it cannot introduce new Java behaviour. Animation controller conditions use a small documented
[expression grammar](/species/animation-controllers#expression-grammar), not arbitrary code.

Anything a pack gets wrong is reported in the log with the file and the reason, and that one file is
skipped. Other species keep loading.

## Next

* [Add a species](/species/quick-start) — the five files, start to finish
* [Species JSON](/species/species-json) — every gameplay field
* [Behaviour components](/species/behaviors) — the AI catalogue
