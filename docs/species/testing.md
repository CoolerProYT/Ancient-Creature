# Testing a species

## Quick loop

```
/reload                              picks up data/ changes
F3 + T                               picks up assets/ changes
/ancientcreature species list        confirm it loaded
/ancientcreature summon mypack:stegosaurus
```

Editing a species and running `/reload` updates attributes, size and AI on creatures already in the
world — no need to kill and respawn.

## Checklist

| Check | How |
| --- | --- |
| It loads | `/ancientcreature species list` shows the id |
| Behaviour resolved as intended | `/ancientcreature species get <id>` lists the final component set |
| Hitbox matches `physical` | F3 + B and look at the box |
| It renders | If invisible, the log names the missing geometry or texture |
| Animations play | Walk it around, hit it, watch state changes |
| Size and growth | Spawn a baby with `{Age:-24000}` and compare |
| Survives a restart | Save, quit, reload — species and variant persist |
| Sounds | Ambient, hurt, death, step, attack |
| Breeding | Feed two adults; an egg is laid and hatches the same species |
| Hunting | `/ancientcreature hunger <targets> 0`, then watch it pick a target |
| Identification GUI | Open the Fossil Identification Chamber — it should be listed and framed |
| Full progression | Fossil → DNA → genome → embryo → egg → capsule |

## Hunger in Jade

Hunger decides whether a predator attacks, and it is otherwise invisible. If
[Jade](https://modrinth.com/mod/jade) is installed, looking at a creature shows it:

```
Tyrannosaurus Rex
Hunger: 14/20
Fed - not hunting
```

The second line only appears once the value has crossed a threshold — red *Hungry - will hunt* at or below
`hunt_threshold`, green *Fed - not hunting* at or above `full_threshold`. In the gap between them the
creature is in the middle of the band, still finishing whatever hunt it started.

Toggle the readout under Jade's own settings, as **Creature Hunger**.

The value comes straight from synchronised entity data, so it is live and needs no server round-trip. It
does not appear until the species' datapack is loaded, because until then the creature is running on
fallback values rather than its own.

Without Jade, [`/ancientcreature hunger`](/species/commands#hunger) reports the same thing and can also set
it.

## Deliberate breakage

Worth doing once so you recognise the messages:

**Disable the datapack, keep the resource pack, `/reload`.** The creature goes inert — no AI, default
size — and logs:

```
No species definition for 'mypack:stegosaurus'. Creatures of this species will stay inert with
default size and attributes until the datapack providing it is loaded.
```

Re-enable and `/reload`: it recovers. The species id is never rewritten, so nothing is lost.

**Break the JSON.** Put `"width": 0` in and `/reload`. The file is named, the reason given, the species
skipped — and every *other* species still loads.

**Rename a bone in the geometry but not the animation.** The animation still plays; the renamed channel
is dropped with a warning naming the animation and the bone.

## Common problems

| Symptom | Likely cause |
| --- | --- |
| Not in `species list` | Datapack half missing, or the file failed to parse — check the log |
| Listed but invisible | `geometry` or `texture` id wrong in the client definition; log says which |
| Renders but in T-pose | No `controller`, or its states play animations that do not exist |
| Texture scrambled | Model exported with per-face UV — re-export with box UV |
| Hitbox wrong size | `physical` in the *datapack*, not `render_scale` in the resource pack |
| Too big/small in the GUI | Tune `gui.scale` in the client definition |
| Ignores you entirely | No behaviour components, the ones chosen do not target players, or the creature is simply **fed** — check `/ancientcreature hunger` |
| Falls through the world on spawn | Aquatic species spawned on land — it needs water, or `find_water` |
| Flying creature walks | `entity_category` is not `"flying"`, so the flight components skip themselves |

## Automated tests

The mod's own tests parse every shipped species, geometry, animation and controller through the real
codecs, and verify the migrated models are geometrically identical to the hand-written ones they
replaced:

```bash
./gradlew :common:test
```

They also check that a broken definition is *rejected* — so if you are contributing a species to the
mod itself, adding it there gets you the same coverage.
