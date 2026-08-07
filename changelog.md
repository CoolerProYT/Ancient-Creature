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
- Added the **Pteranodon**, the first flying creature and the first species built entirely on the
  data-driven pipeline — five resource files, no Java. It nests on beaches, hunts on the wing, eats cod
  and salmon, and breeds like the land species.

  | | |
  | --- | --- |
  | hitbox / eye height | 1.5 × 1.4 / 0.85 |
  | health / attack | 26 / 5 |
  | wingspan | 4.6 blocks |
  | clips | `fly`, `glide`, `idle`, `walk`, `dive`, `screech`, `death` |

  Two gaps in the flying category surfaced while building it, both fixed:

  - `query.is_on_ground` is a new animation-controller query. Without it a flyer cannot tell perched
    from airborne, so it would either freeze mid-air with folded wings or flap while standing.
  - `minecraft:flying_speed` is now on the creature's attribute supplier, so species JSON can tune
    flight speed. Previously setting it was ignored with a warning and flyers fell back to ground speed.

### Fixes

- **Fixed a crash loading any world after the first**, which reported `Attempted to register two reload
  listeners for the same key: ancientcreature:species`. NeoForge fires its reload-listener and command
  events once per world load rather than once per launch, and the registration list was appended to each
  time, so the second world you joined registered the species listener twice. The registrations are now
  collected once and replayed against each new registrar, and reload listeners are keyed by id so a
  duplicate is impossible by construction.
- **Flying creatures are no longer stuck at one altitude.** `ancientcreature:fly` delegated to vanilla's
  `WaterAvoidingRandomFlyingGoal`, whose wander box is hard-coded to 8 blocks out and 7 up, so a flyer
  could never pick a waypoint far enough above itself to climb. It now takes:

  | field | default | meaning |
  | --- | --- | --- |
  | `wander_range` | 16 | horizontal spread of one hop, in blocks |
  | `vertical_range` | 12 | vertical spread of one hop |
  | `min_altitude` | 5 | floor of the flight band, above the terrain |
  | `max_altitude` | 32 | ceiling of the flight band, above the terrain |
  | `interval` | 10 | reciprocal chance per tick of picking a new waypoint |

  The band is measured from the ground column under each candidate waypoint rather than from the
  creature, so a species holds its altitude over hills instead of drifting into them. `max_altitude`
  below `min_altitude` is rejected when the species loads. The Pteranodon soars at 8–48 blocks.

### Hunger

**Predators no longer attack everything that walks past.** Every creature now has a hunger meter, and
predatory components only pick a target once it falls far enough. A fed Tyrannosaurus Rex ignores you.

Hunger is a *fullness* meter, like the player's food bar: it starts full, drops over time, and a low value
means a hungry creature. Configured per species under a new optional `hunger` block:

| Field | Default | Meaning |
| --- | --- | --- |
| `max` | 20.0 | Full meter |
| `decay_interval` | 1200 | Ticks to lose one point — 20 minutes from full to empty |
| `hunt_threshold` | 10.0 | Start hunting at or below this |
| `full_threshold` | 18.0 | Stop hunting at or above this |
| `food_value` | 6.0 | Restored per item hand-fed |
| `kill_value` | 8.0 | Restored per prey killed |
| `starve_damage` | 0.0 | Damage at zero hunger; `0` disables starving, which is the default |
| `starve_interval` | 200 | Ticks between starvation ticks |

The two thresholds are separate on purpose. With one, a predator would sit exactly on the boundary after
its first bite and flicker between hunting and idling every tick; the gap makes it commit to a hunt and
then genuinely stop.

**Only prey selection is gated. Defence never is.** Gating retaliation or the attack goals would leave a
fed creature unable to fight back, which is worse than attacking at random.

| Gated (`requires_hunger` default) | Never gated |
| --- | --- |
| `hunt_animals` — `true` | `defensive_retaliation` — fighting back |
| `aquatic_predator` — `true` | `hunt_hostiles` — driving off zombies is defence, not a meal |
| `territorial` — `false`, see below | `melee_attack`, `charge_attack`, `roar_attack` |

`territorial` defaults to ungated, because guarding your space is defence. But with `require_weapon: false`
it stops being defence and becomes hunting players on sight — which is exactly what made the Tyrannosaurus
Rex feel random, since it charged anyone within 32 blocks forever. The `apex_predator` and `flying_predator`
profiles now set `requires_hunger: true` there.

Creatures refill by killing prey, by being hand-fed a `diet` item (which still breeds them as before), or
by finishing a graze — `ancientcreature:graze` gained a `hunger_value`, and that is how herbivores feed
themselves. Every `requires_hunger` flag can be set to `false` to restore the old always-hunting behaviour.

Hunger is saved per creature; a creature from a world saved before this update starts full.

Because a mechanic that decides whether something attacks you should not be invisible, it is exposed three
ways:

- **Jade** — looking at a creature shows `Hunger: 14/20` and, once past a threshold, a red *Hungry - will
  hunt* or green *Fed - not hunting*. Toggle it as **Creature Hunger** in Jade's settings.
- **`/ancientcreature hunger`** — reports the same, and can set the value to cross the threshold on demand.
- **Animation controllers** — two new queries, `query.hunger` and `query.is_hungry`.

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
/ancientcreature hunger <targets>          how fed they are, and whether that makes them hunt
/ancientcreature hunger <targets> <value>  set it, to cross the threshold without waiting
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