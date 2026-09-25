# Ancient Creature wiki

VitePress site for the mod. Recipes, advancements, fossil parts, chisels, DNA levels, species stats, dig sites and config defaults are read from the mod itself, so regenerate the mod's data before building when it changes.

```bash
./gradlew :neoforge:runData   # from the repository root, when mod data changed
cd docs
npm install
npm run dev                   # syncs data, then serves http://localhost:5173
npm run build                 # syncs data, then builds to .vitepress/dist
```

`npm run sync` (run automatically by `dev` and `build`) writes `.vitepress/data/data.json`, which is git-ignored. It reads:

- datagen output in `common/src/generated/resources` (recipes, advancements, fossil parts, loot, lang, biome tags),
- the shipped species in `common/src/main/resources/data/ancientcreature/ancientcreature/species`,
- a few constants from the Java source: chisel damage (`ModItems`), DNA integrity (`DNAIntegrityLevel`) and config defaults (`ModCommonConfig`).

## Textures

Item icons load from `https://storage.googleapis.com/coolerpromc/textures/`, not from this folder:

- vanilla items from `minecraft/<item>.png`,
- mod items from `ancientcreature/<name>.png`, uploaded at 1024×1024 (nearest-neighbour). Textures in sub folders are uploaded flat as `<folder>_<name>`, for example `fossil_part_skull`.

Upload a new texture there before syncing, or the wiki shows the item's initials instead. Machines and the rock pile have no flat texture, so their icons are rendered from the in-game models and committed in `public/icons/` (upload those too).

## Layout

```
docs/
  .vitepress/config.mts      site config, nav and sidebar
  .vitepress/theme/          theme, data helpers and Vue components
  guide/                     player guide
  species/                   custom species (datapack / resource pack) docs
  scripts/sync-data.mjs      reads data from the mod
```

## Publishing

`.github/workflows/docs.yml` builds the site and deploys it to GitHub Pages on every push to the repository's default branch, or when run by hand from the Actions tab. Enable it once under **Settings > Pages > Source: GitHub Actions**.
