# Keeping creatures

A revived creature is yours to command, but it is still an animal with needs. Keep it comfortable and it heals and breeds. Neglect it and a large one will try to break out.

## Giving orders

<ItemSlot id="ancientcreature:horn_whistle" label size="lg" />

<div class="ac-recipes">
<RecipeCard id="horn_whistle" />
<RecipeCard id="horn_whistle_from_goat_horn" />
</div>

The **Horn Whistle** gives orders to creatures you own. A Prehistoric Horn and a Goat Horn both work for crafting it.

| Use | Effect |
| --- | --- |
| On one of your creatures | Cycles it between **Roam**, **Follow** and **Stay** |
| In the air | Every creature of yours within 48 blocks that isn't staying comes to you and follows |
| Sneak, in the air | Every creature of yours within 48 blocks stays where it is |

| Order | What the creature does |
| --- | --- |
| **Roam** | Lives normally, but stays within 24 blocks of the spot where you gave the order. New creatures start out roaming. |
| **Follow** | Walks, swims or flies after you, and teleports to catch up once it is 24 blocks behind (aquatic creatures don't teleport). Creatures that can fight also attack whatever hurts you and whatever you attack, but never your other creatures. |
| **Stay** | Sits still until told otherwise. Flyers land. |

The whistle only works on creatures you own. With Jade installed, look at a creature to see its current order.

## Comfort

Every adult has a **comfort** level from 0 to 100. A few times a minute it looks at its situation and comfort drifts towards a target, so one bad moment won't swing it but a lasting problem always shows. With Jade installed, look at a creature to see its comfort and its biggest problem.

| Comfort | State | Effect |
| --- | --- | --- |
| 75–100 | Content | Slowly heals, as long as it isn't hungry |
| 50–74 | Settled | Can breed |
| 30–49 | Uneasy | Won't breed |
| 0–29 | Distressed | Large creatures try to [break out](#enclosures) |

What it cares about:

- **Hunger**: a hungry creature is unhappy and a starving one is miserable. A full one is a little happier.
- **Space**: room to walk around, in blocks of floor it can reach. Each species wants its own amount, and the bigger the animal, the bigger the pen. Aquatic creatures count water instead of floor. Flyers never feel cramped.
- **Company**: herd animals want a group of their own kind nearby (between a minimum and maximum), pairs want exactly one companion, and solitary animals dislike others of their kind. Solitary babies don't mind company.
- **Climate**: ice-age animals want cold biomes, others want warm or temperate ones. Some, like the big sea predators, don't care.
- **Pain**: being shocked by an [electric fence](#electric-fence) or wounded recently.
- **Water**: an aquatic creature stranded on land is very unhappy.
- **Care**: hand-feeding one of its diet items gives a boost for five minutes. Babies are always a little happier.

Each creature's [genome](./reviving#genome-traits) also has a temperament: calm animals settle more easily, fierce ones are harder to keep content.

The [Creatures](./creatures) page lists each species' social needs and climate.

::: tip Turning it off
Set `Creatures.comfort` to `false` in the [config](./compat#config) to skip comfort entirely. Creatures then always breed and never break out.
:::

## Enclosures

A distressed adult that's wider than 1.2 blocks will now and then pick a direction and charge straight along it, smashing weak barriers in its way: wooden fences and gates, walls, glass and glass panes. Creatures told to **Stay** never do this.

Two fences hold any creature.

### Reinforced fence

<div class="ac-recipes">
<RecipeCard id="reinforced_fence" />
<RecipeCard id="reinforced_fence_gate" />
</div>

An iron fence and gate. No creature breaks through them, and they need an iron pickaxe or better to mine.

### Electric fence

<RecipeCard id="electric_fence" />

A wire fence that shocks whatever touches it while it carries a charge. Power it with redstone: a segment next to a redstone signal is fully charged, and each connected segment carries one less than its neighbour, so one power source charges up to 15 segments in each direction. A charged fence crackles and sparks.

- A shock deals up to 4 damage (2 at the weakest charge) and throws whatever touched it back. **Players get shocked too.**
- Creatures won't path through it and never smash it.
- A shock hurts the creature's comfort for two minutes, so a pen that's too small with an electric fence around it will keep its animals unhappy.
- It connects to other electric fences, to fence gates and to solid blocks, so a reinforced gate makes a good way in.

Shock damage is set by `Containment.electricFenceDamage` in the [config](./compat#config).

## Population limit

To keep servers running smoothly, a new creature can't appear if there are already 64 revived creatures within 64 blocks. Breeding waits, eggs hold off hatching and capsules refuse to open until there's room. Both numbers are in the [config](./compat#config) (`Creatures.populationCap`, `Creatures.populationRadius`).

## Peaceful species

List species in `Creatures.passiveSpecies` to make them never start a fight. They still defend themselves when attacked. Use the full id, for example `"ancientcreature:tyrannosaurus_rex"`.
