# Getting started

Ancient Creature is a path from a buried bone to a living creature. You dig up a fossil, clean it, work out what it belongs to, pull its DNA, build a complete genome, grow an embryo and hatch it.

## Install

1. Install [Fabric](https://fabricmc.net/) with Fabric API, or [NeoForge](https://neoforged.net/), for Minecraft **26.1** or newer.
2. Put the Ancient Creature jar in your `mods` folder.
3. Optional: add [JEI](https://modrinth.com/mod/jei) to see every machine step in game, and [Jade](https://modrinth.com/mod/jade) to check eggs and creature hunger by looking at them.

## The whole journey

<ProcessChain />

Each machine does one job and passes its result to the next. Every machine accepts hoppers, so the whole lab can be automated once you have the pieces.

## Your first creature

**1. Make a chisel.** Fossils only drop when mined with a chisel. A stone one works, but it chips off 40% of every fossil's quality. See [Chisels](./fossils#chisels).

<RecipeCard id="stone_chisel" />

**2. Find a fossil.** Look for **Fossil Ore** in stone between Y -64 and 50, or for **rock piles** on the surface with bones or an egg sticking out. Mine them with the chisel. See [Finding fossils](./fossils).

**3. Clean it** in a [Fossil Cleaning Table](./cleaning-and-identifying#cleaning) with a brush.

**4. Identify it** in a [Fossil Identification Chamber](./cleaning-and-identifying#identification). The first fossil of each species can fail and lose some quality. After that, that species always succeeds.

**5. Extract its DNA** in a [DNA Extractor](./dna-and-genomes#dna-extractor) with Extraction Fluid and a Sample Vial.

**6. Sequence the DNA** onto a Genome Cartridge in the [Genome Sequencer](./dna-and-genomes#genome-sequencer). Keep adding DNA of the same species until the cartridge reaches 100%.

**7. Grow an embryo.** Put the completed cartridge, an Artificial Egg and Nutrient Solution into the [Embryogenesis Chamber](./reviving#embryogenesis-chamber).

**8. Incubate the egg** in the [Incubator](./reviving#incubator), then use the **Baby Creature Capsule** on the ground to release your baby. It's yours: it grows up, and once it's an adult you can [ride it](./riding).

::: tip Plan ahead
Better fossils make better DNA, and better DNA fills a genome in fewer samples. Teeth, skulls and egg fossils are the best finds. A Diamond or Netherite Chisel keeps most or all of their quality.
:::

## Species Journal

Press <kbd>J</kbd> to open the Species Journal. It lists every species that has been identified on the server, with its habitat, diet, size, speed, health, attack, incubation time and how long it takes to grow up. Species nobody has identified yet show as locked.
