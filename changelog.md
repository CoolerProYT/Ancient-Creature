## 26.1.2.4

### Data-driven species

- Creatures are defined by a datapack and a resource pack instead of Java — no new entity type, entity class, renderer, model class, animation class or loader-specific code.
- Five files per species:

  ```
  data/<ns>/ancientcreature/species/<name>.json                              gameplay
  assets/<ns>/ancientcreature/species/<name>.json                            appearance
  assets/<ns>/ancientcreature/geo/<name>.geo.json                            model
  assets/<ns>/ancientcreature/animations/<name>.animation.json               animations
  assets/<ns>/ancientcreature/animation_controllers/<name>.controller.json   when to play what
  ```

- Blockbench **Bedrock Entity** and **Bedrock Animation** exports load directly.
- `/reload` updates attributes, size and AI on already-spawned creatures; F3+T reloads models, animations and controllers.
- Broken files are logged with the file name and the reason, then skipped — other species still load.
- Dedicated servers never load models or textures.

### Creatures

- Triceratops, Tyrannosaurus Rex and Megalodon converted. Stats, AI, sounds, growth and animations unchanged, the geometry is verified vertex and UV identical to the old hand-written models.
- All species now spawn as one entity, `ancientcreature:ancient_creature`.
- Editing `physical` now resizes creatures already in the world on `/reload`, previously the resize was skipped unless the growth scale also changed.
- Added flying support. `"entity_category": "flying"` now gives real flight navigation, hovering movement and no fall damage, with the `ancientcreature:fly` and `ancientcreature:circle_target` components and the `flying_passive` / `flying_predator` profiles. It was previously a label with nothing behind it.
- Added the **Pteranodon** — the first flyer, and the first species built entirely from resource files. Nests on beaches, hunts on the wing, eats cod and salmon, breeds like the land species. 1.5 × 1.4 hitbox, 0.85 eye, 26 health, 5 attack, 4.6-block wingspan, soars at 8–48 blocks.

### Hunger

- Every creature has a hunger meter, and predatory components only pick a target once it falls far enough. A fed Tyrannosaurus Rex ignores you.
- A fullness meter like the player's food bar: starts full, decays, low means hungry. Optional `hunger` block per species — `max` (20), `decay_interval` (1200), `hunt_threshold` (10), `full_threshold` (18), `food_value` (6), `kill_value` (8), `starve_damage` (0, starving off by default), `starve_interval` (200).
- Two separate thresholds so a predator commits to a hunt instead of flickering on the boundary after its first bite.
- Gated by hunger: `hunt_animals` and `aquatic_predator` (`requires_hunger` defaults to `true`), `territorial` (defaults to `false`).
- Never gated: `defensive_retaliation`, `hunt_hostiles`, `melee_attack`, `charge_attack`, `roar_attack` — a fed creature must still be able to fight back.
- `apex_predator` and `flying_predator` set `requires_hunger: true` on `territorial`, which with `require_weapon: false` was what made the T-Rex charge anyone within 32 blocks forever.
- Creatures refill by killing prey, by being hand-fed a `diet` item (which still breeds them), or by grazing — `ancientcreature:graze` gained `hunger_value`.
- Saved per creature; creatures from older worlds start full. Every `requires_hunger` can be set to `false` for the old always-hunting behaviour.
- Visible three ways: Jade (**Creature Hunger**), `/ancientcreature hunger`, and the `query.hunger` / `query.is_hungry` animation queries.

### Behaviour

- AI is assembled from a fixed catalogue — datapacks select and configure components, they cannot introduce code.
- 6 profiles: `passive`, `defensive_herbivore`, `apex_predator`, `aquatic_predator`, `flying_passive`, `flying_predator`.
- 22 components covering movement, idling, breeding, herding, grazing, melee, charging, roaring, target selection and flight.
- A component listed on a species replaces the profile's component of the same type, so one goal can be retuned without restating the rest.
- Most attack and target components accept `adults_only`.
- Creatures never attack their own species or their owner.

### API for other mods

- `AncientCreatureApi` and `AncientCreatureClientApi` are public entry points. Register during mod initialization; the registries freeze once species data loads.
- `registerBehavior(...)` / `registerBehaviorProfile(...)` — your own components and profiles, usable from any species JSON.
- `registerNumberQuery(...)` / `registerStringQuery(...)` — custom `query.*` values for animation controllers.
- `getSpecies(...)`, `loadedSpecies()`, `onSpeciesReloaded(...)`, `spawn(...)`, `speciesOf(...)`.
- Baked geometry access for custom rendering.

### Commands

All require permission level 2 (gamemaster).

```
/ancientcreature summon <species> [<pos>] [<baby>] [<variant>]
/ancientcreature hunger <targets> [<value>]
/ancientcreature species list | get <id> | validate | reload
/ancientcreature behaviors
```

- `summon` tab-completes loaded species ids and refuses an id with no definition, instead of quietly spawning an inert creature the way hand-written `/summon … {Species:"…"}` NBT does.

### Compatibility

**Back up your world before updating.**

- Creatures already placed in your world are lost — the `ancientcreature:triceratops`, `ancientcreature:tyrannosaurus_rex` and `ancientcreature:megalodon` entity ids are no longer registered, and Minecraft silently drops entities whose type it does not recognise.
- Everything that is not a live creature loads unchanged: fossils, DNA samples, genome cartridges, fertilized eggs, capsules and the identified-species list all still read. Hatching an egg or using a capsule gives the same creature back, now data-driven.
- Species values are now written fully qualified (`ancientcreature:triceratops`), so downgrading after loading a world will lose species data.

### Removed

- `Triceratops`, `TyrannosaurusRex`, `Megalodon` and `OwnedWaterAncientCreature`, with their renderers, models, render states, Java animation definitions and model layers.
- The three per-species entity type registrations.
