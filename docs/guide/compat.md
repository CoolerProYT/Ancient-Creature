# JEI, Jade & config

## JEI

With [JEI](https://modrinth.com/mod/jei) installed, every step has its own recipe category, with notes on chances and timings:

- **Fossil Part Hunting**: which fossils drop from fossil ore, deepslate fossil ore, amber ore, frozen fossils, rock piles, dig sites and the Sifter, for every species and chisel, with the biomes and completeness ranges.
- One category per machine: what it takes and makes, including identification fail chances, DNA integrity results, incubation time per species and every [hybrid](./reviving#hybrids) pairing.

Look up any machine or item to jump to its pages.

## Jade

With [Jade](https://modrinth.com/mod/jade) installed, looking at:

- a **creature** shows its hunger and whether it's hungry enough to hunt, its [comfort](./keeping-creatures#comfort) and biggest problem, its genome fidelity and temperament, whether it's sterile or frail, and (for your own creatures) its current [order](./keeping-creatures#giving-orders),
- a **bred egg** shows how long until it hatches.

Both can be turned off in Jade's plugin settings (*Creature Hunger* and *Egg Data*).

## Config

Settings are in `config/ancientcreature-common.toml`. The file is watched, so changes apply without a restart.

<ConfigTable />

Times are in ticks (20 ticks = 1 second). The incubation multiplier scales every species' own incubation time in the Incubator: `0.5` hatches twice as fast, `2` takes twice as long. Eggs laid by breeding creatures are not affected.
