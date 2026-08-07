## 26.1.2.4

### Data-Driven Species

Creatures are no longer defined in Java. A species is now a resource id (`ancientcreature:triceratops`,
`yourpack:stegosaurus`) described entirely by a datapack and a resource pack — no new entity type, entity
class, renderer, model class, animation class or loader-specific code.

Adding a creature now takes five files:

```
data/<ns>/ancientcreature/species/<name>.json                              gameplay
assets/<ns>/ancientcreature/species/<name>.json                            appearance
assets/<ns>/ancientcreature/geo/<name>.geo.json                            model
assets/<ns>/ancientcreature/animations/<name>.animation.json               animations
assets/<ns>/ancientcreature/animation_controllers/<name>.controller.json   when to play what
```

- Species definitions reload with `/reload`. Attributes, size and AI update on already-spawned creatures.
- Models, animations and controllers reload with F3+T.
- Blockbench **Bedrock Entity** and **Bedrock Animation** exports load directly.
- Broken files are reported with the file name and the reason, then skipped. Other species still load.
- Dedicated servers never load models or textures.

### Creatures

- **Triceratops**, **Tyrannosaurus Rex** and **Megalodon** are now fully data-driven. Their stats,
  hitboxes, AI, sounds, growth, models and animations are unchanged — the converted geometry is verified
  vertex-for-vertex and UV-for-UV against the old hand-written models.
- All three now spawn as one generic entity, `ancientcreature:ancient_creature`.
- **Fixed hitboxes and eye heights on all three.** These were inherited unchanged from the old entity
  types, where nothing ever compared them against the models. The Tyrannosaurus Rex's eye sat 0.34
  blocks above the top of its own model and the Megalodon's 0.5 above the top of its head, which made
  both of them see over blocks they were standing behind.

  | | hitbox (was → now) | eye height (was → now) |
  | --- | --- | --- |
  | Triceratops | 1.2 × 1.8 → 1.8 × 2.2 | 1.4 (unchanged) |
  | Tyrannosaurus Rex | 1.5 × 2.7 → 1.2 × 2.0 | 2.25 → 1.7 |
  | Megalodon | 2.8 × 1.6 (unchanged) | 1.0 → 0.35 |

  Editing `physical` now resizes creatures already in the world on `/reload`; previously the resize
  was skipped unless the growth scale also changed.
- Added **flying creature support**. `"entity_category": "flying"` now gives real flight navigation,
  hovering movement and no fall damage, with the `ancientcreature:fly` and
  `ancientcreature:circle_target` components and the `flying_passive` / `flying_predator` profiles.
  Previously it was a label with nothing behind it.

### Behaviour

Species AI is assembled from a fixed catalogue of components, either individually or through a named
profile. Datapacks can only select and configure them — they cannot introduce arbitrary code.

- Profiles: `passive`, `defensive_herbivore`, `apex_predator`, `aquatic_predator`, `flying_passive`,
  `flying_predator`
- 22 components covering movement, idling, breeding, herding, grazing, melee, charging, roaring,
  target selection and flight
- A component listed on a species replaces the profile's component of the same type, so one goal can be
  retuned without restating the rest
- Most attack and target components accept `adults_only`
- Creatures never attack their own species or their owner

### API for other mods

`AncientCreatureApi` and `AncientCreatureClientApi` are now public entry points:

- `registerBehavior(...)` — add your own behaviour component, usable from any species JSON
- `registerBehaviorProfile(...)` — add a named component bundle
- `registerNumberQuery(...)` / `registerStringQuery(...)` — add `query.*` values for animation controllers
- `getSpecies(...)`, `loadedSpecies()`, `onSpeciesReloaded(...)`, `spawn(...)`, `speciesOf(...)`
- Baked geometry access for custom rendering

Register during mod initialization; the registries freeze once species data loads.

### Commands

```
/ancientcreature summon <species> [<pos>] [<baby>] [<variant>]
/ancientcreature species list        every loaded species with its category and size
/ancientcreature species get <id>    size, growth, attributes, resolved behaviour list
/ancientcreature species validate    re-run validation and report problems
/ancientcreature species reload      reload datapacks
/ancientcreature behaviors           every component type and profile
```

`summon` tab-completes loaded species ids and refuses an id with no definition, instead of quietly
spawning an inert creature the way hand-written `/summon … {Species:"…"}` NBT does.

All require permission level 2 (gamemaster).

### Compatibility

**Back up your world before updating.**

- **Creatures already placed in your world are lost.** The `ancientcreature:triceratops`,
  `ancientcreature:tyrannosaurus_rex` and `ancientcreature:megalodon` entity ids are no longer
  registered, and Minecraft silently drops entities whose type it does not recognise.
- Everything that is not a live creature loads unchanged. The old species enum was saved as its bare
  name (`"triceratops"`), and that still reads correctly everywhere — fossils, DNA samples, genome
  cartridges, fertilized eggs, capsules and the identified-species list. Hatching an egg or using a
  capsule gives you the same creature back, now data-driven.
- Species values are now written fully qualified (`ancientcreature:triceratops`). Older versions of the
  mod cannot read those, so downgrading after loading a world will lose species data.

### Removed

- `Triceratops`, `TyrannosaurusRex`, `Megalodon` and `OwnedWaterAncientCreature` entity classes
- Their renderers, models, render states and Java animation definitions
- Their entity model layers
- The `ancientcreature:triceratops`, `ancientcreature:tyrannosaurus_rex` and
  `ancientcreature:megalodon` entity type registrations — every species now spawns as
  `ancientcreature:ancient_creature`

### Documentation

- New wiki (VitePress, maintained outside this repository) covering the species format, behaviour
  components, Bedrock geometry and animation support, controllers, commands and the API
- `docs/data-driven-species.md` now points at the wiki instead of duplicating it

---