# DNA & genomes

An identified fossil still holds only scraps of DNA. Extract them into a sample, then piece samples together on a Genome Cartridge until the creature's genome is complete.

## DNA Extractor

<RecipeCard id="dna_extractor" />

The extractor needs three things: **Extraction Fluid**, an empty **Sample Vial** and an **identified, clean** fossil.

<MachineRecipe
  machine="ancientcreature:dna_extractor"
  :inputs="['ancientcreature:extraction_fluid|Uses 1 of 4 durability', 'ancientcreature:sample_vial', 'ancientcreature:fossil_part/tooth|Identified']"
  :outputs="['ancientcreature:dna_sample/stable']"
  config="DNAExtractor.extractingTick"
/>

<div class="ac-recipes">
<RecipeCard id="extraction_fluid" />
<RecipeCard id="sample_vial" />
</div>

Extraction Fluid lasts for 4 extractions. The fossil is always used up.

### DNA quality

The quality of the sample, its **integrity**, comes from an integrity score:

**integrity score = fossil completeness + fossil part bonus**

Teeth, skulls and egg fossils carry a bonus; claws carry a penalty. See the bonus for every part on [Finding fossils](./fossils#fossil-parts).

<DnaTable />

If the score is too low, the extraction still uses the fossil and a point of Extraction Fluid, but gives no sample and keeps the vial. The [calculator](./fossils#try-it) shows which sample a fossil will give before you spend it.

## Genome Sequencer

<RecipeCard id="genome_sequencer" />

The sequencer writes DNA onto a Genome Cartridge. Each sample adds a random amount of genome depending on its integrity (the **Genome added** column above).

<RecipeCard id="genome_cartridge_blank" />

<MachineRecipe
  machine="ancientcreature:genome_sequencer"
  :inputs="['ancientcreature:dna_sample/stable', 'ancientcreature:genome_cartridge_blank']"
  :outputs="['ancientcreature:genome_cartridge_filled|Completeness: 8–15%']"
  config="GenomeSequencing.sequencingTick"
/>

1. A **blank** cartridge takes the species of the first sample and becomes a **filled** cartridge.
2. Keep feeding samples of the **same species**. With a sample of another species in the slot, the sequencer just waits.
3. At 100% the cartridge becomes **completed**, ready for the [Embryogenesis Chamber](./reviving#embryogenesis-chamber).

<MachineRecipe
  machine="ancientcreature:genome_sequencer"
  :inputs="['ancientcreature:dna_sample/preserved_embryo', 'ancientcreature:genome_cartridge_filled|Completeness: 90%']"
  :outputs="['ancientcreature:genome_cartridge_completed']"
  config="GenomeSequencing.sequencingTick"
/>

The cartridge stays in its slot between samples, so a hopper feeding DNA from the side can fill it hands-free.

## Automation

| Machine | Top | North & south | East & west | Bottom |
| --- | --- | --- | --- | --- |
| DNA Extractor | Sample Vial | Extraction Fluid | Fossil | DNA sample (extract) |
| Genome Sequencer | Cartridge | DNA sample | DNA sample | Cartridge |
