// Pulls wiki data straight from the mod so the docs never drift from the game:
// datagen output (recipes, advancements, fossil parts, loot, lang, biome tags), the shipped species JSON,
// a few constants read from the Java source (chisels, DNA integrity, config defaults) and the mod's own textures.
// Run `./gradlew :neoforge:runData` first when the mod's data changes.
import { existsSync, readdirSync, readFileSync, writeFileSync, mkdirSync } from 'node:fs'
import { basename, dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const docs = join(dirname(fileURLToPath(import.meta.url)), '..')
const root = join(docs, '..')
const generated = join(root, 'common/src/generated/resources')
const main = join(root, 'common/src/main/resources')
const java = join(root, 'common/src/main/java/com/coolerpromc/ancientcreature')
const assets = join(main, 'assets/ancientcreature')
const data = join(generated, 'data/ancientcreature')

if (!existsSync(generated)) {
  console.error(`No datagen output at ${generated}. Run ./gradlew :neoforge:runData first.`)
  process.exit(1)
}

const readJson = (file) => JSON.parse(readFileSync(file, 'utf8'))
const readText = (file) => readFileSync(file, 'utf8')
const jsonFiles = (dir) => (existsSync(dir) ? readdirSync(dir).filter((f) => f.endsWith('.json')).sort() : [])
const pngNames = (dir) => (existsSync(dir) ? readdirSync(dir).filter((f) => f.endsWith('.png')).map((f) => basename(f, '.png')).sort() : [])
const text = (component) => (typeof component === 'string' ? component : component?.translate ?? component?.text ?? '')
const title = (id) => id.split('_').map((word) => word.charAt(0).toUpperCase() + word.slice(1)).join(' ')

const lang = readJson(join(generated, 'assets/ancientcreature/lang/en_us.json'))

// Item names for mod items, plus the variant ids the wiki uses for components the game shows in the name:
// ancientcreature:fossil_part/<part>, ancientcreature:fossil_part/dirty_<part> and ancientcreature:dna_sample/<level>.
const names = {}
for (const [key, value] of Object.entries(lang)) {
  let match = key.match(/^(item|block)\.ancientcreature\.([a-z0-9_]+)$/)
  if (match) names[`ancientcreature:${match[2]}`] = value
  match = key.match(/^fossilPart\.ancientcreature\.([a-z0-9_]+)$/)
  if (match && match[1] !== 'empty') {
    names[`ancientcreature:fossil_part/${match[1]}`] = value
    names[`ancientcreature:fossil_part/dirty_${match[1]}`] = `${lang['name.ancientcreature.dirty']} ${value}`
  }
  match = key.match(/^dna\.ancientcreature\.([a-z0-9_]+)$/)
  if (match) names[`ancientcreature:dna_sample/${match[1]}`] = `${lang['item.ancientcreature.dna_sample']} (${value})`
}

// Mod textures are hosted at 1024x1024 alongside the vanilla renders; upload new ones there before syncing.
// The hosted bucket is flat, so textures in sub folders are named <folder>_<name>.
// Blocks with a 3D model have no flat texture, so their icons are rendered and kept in public/icons/.
const HOSTED_TEXTURES = 'https://storage.googleapis.com/coolerpromc/textures/ancientcreature'
const textures = {}
for (const name of pngNames(join(assets, 'textures/item'))) {
  const variant = name.match(/^dna_sample_(.+)$/)
  textures[variant ? `ancientcreature:dna_sample/${variant[1]}` : `ancientcreature:${name}`] = `${HOSTED_TEXTURES}/${name}.png`
}
for (const name of pngNames(join(assets, 'textures/item/fossil_part'))) {
  textures[`ancientcreature:fossil_part/${name}`] = `${HOSTED_TEXTURES}/fossil_part_${name}.png`
}
textures['ancientcreature:fossil_ore'] = `${HOSTED_TEXTURES}/fossil_ore.png`
for (const name of pngNames(join(docs, 'public/icons'))) {
  textures[`ancientcreature:${name}`] = `${HOSTED_TEXTURES}/${name}.png`
}
// Items whose look depends on a component fall back to a representative variant.
textures['ancientcreature:dna_sample'] ??= textures['ancientcreature:dna_sample/stable']
textures['ancientcreature:fossil_part'] ??= textures['ancientcreature:fossil_part/skull']

const ingredient = (value) => {
  if (typeof value === 'string') return value
  if (Array.isArray(value)) return ingredient(value[0])
  if (value?.item) return value.item
  if (value?.tag) return `#${value.tag}`
  return '?'
}

const recipes = jsonFiles(join(data, 'recipe')).map((file) => {
  const json = readJson(join(data, 'recipe', file))
  const recipe = { id: `ancientcreature:${basename(file, '.json')}`, type: json.type, result: { id: json.result?.id, count: json.result?.count ?? 1 } }
  if (json.type === 'minecraft:crafting_shaped') {
    recipe.pattern = json.pattern
    recipe.key = Object.fromEntries(Object.entries(json.key).map(([symbol, value]) => [symbol, ingredient(value)]))
  } else if (json.type === 'minecraft:crafting_shapeless') {
    recipe.ingredients = json.ingredients.map(ingredient)
  } else if (json.type === 'minecraft:smithing_transform') {
    recipe.template = ingredient(json.template)
    recipe.base = ingredient(json.base)
    recipe.addition = ingredient(json.addition)
  } else {
    recipe.ingredient = ingredient(json.ingredient)
    recipe.cookingTime = json.cookingtime
    recipe.experience = json.experience
  }
  return recipe
})

const advancements = jsonFiles(join(data, 'advancement/progression')).map((file) => {
  const json = readJson(join(data, 'advancement/progression', file))
  const display = json.display ?? {}
  return {
    id: `ancientcreature:progression/${basename(file, '.json')}`,
    parent: json.parent ?? null,
    icon: display.icon?.id ?? display.icon?.item ?? null,
    title: lang[text(display.title)] ?? text(display.title),
    description: lang[text(display.description)] ?? text(display.description),
    frame: display.frame ?? 'task',
  }
})

const fossilParts = jsonFiles(join(data, 'ancientcreature/fossil_part')).map((file) => {
  const json = readJson(join(data, 'ancientcreature/fossil_part', file))
  const id = basename(file, '.json')
  return {
    id,
    name: lang[`fossilPart.ancientcreature.${id}`] ?? title(id),
    completenessMin: json.completeness.min,
    completenessMax: json.completeness.max,
    dnaBonus: json.dnaExtractingBonus,
    identifyFailChance: json.identifyFailChance,
    damageRate: json.fossilDamageRate,
  }
})

// Chisel stats are code, not data: ModItems registers each chisel with its tool material and the share of
// completeness it knocks off every fossil it digs up.
const DURABILITY = { WOOD: 59, STONE: 131, COPPER: 190, IRON: 250, GOLD: 32, DIAMOND: 1561, NETHERITE: 2031 }
const chisels = [...readText(join(java, 'item/ModItems.java')).matchAll(
  /registerItem\("([a-z_]+_chisel)".*?chisel\(p, ToolMaterial\.([A-Z]+), [-\d.f]+, [-\d.f]+, ([\d.]+)f?\)/g,
)].map(([, id, material, damage]) => ({ id: `ancientcreature:${id}`, material: material.toLowerCase(), durability: DURABILITY[material], damageRate: Number(damage) }))

// DNA integrity: the genome completeness each level adds, and the score thresholds that pick the level.
const integritySource = readText(join(java, 'item/DNAIntegrityLevel.java'))
const integrityThresholds = [...integritySource.matchAll(/score <=? ([\d.]+)f/g)].map(([, value]) => Number(value))
const dnaLevels = [...integritySource.matchAll(/([A-Z_]+)\("([^"]+)", UniformInt\.of\((\d+), (\d+)\)\)/g)].map(([, key, name, min, max], index) => ({
  id: key.toLowerCase(),
  name,
  genomeMin: Number(min),
  genomeMax: Number(max),
  // Score above the previous threshold (the first threshold is the minimum) up to this level's threshold.
  scoreMin: integrityThresholds[index],
  scoreMax: integrityThresholds[index + 1] ?? 1,
}))

const config = [...readText(join(java, 'config/ModCommonConfig.java')).matchAll(
  /define(Int|Float)\("([^"]+)", ([\d.]+)f?, [^,]+, [^,]+, "([^"]*)"\)/g,
)].map(([, type, key, value, comment]) => ({ key, type: type.toLowerCase(), default: Number(value), comment: comment.replace(/\s*\[\]$/, '') }))

const biomeTag = (tag) => {
  const [namespace, path] = tag.replace(/^#/, '').split(':')
  const file = join(generated, 'data', namespace, 'tags/worldgen/biome', `${path}.json`)
  return existsSync(file) ? readJson(file).values : []
}

const speciesDir = join(main, 'data/ancientcreature/ancientcreature/species')
const species = jsonFiles(speciesDir).map((file) => {
  const id = basename(file, '.json')
  const json = readJson(join(speciesDir, file))
  const biomes = typeof json.spawn?.biomes === 'string' ? biomeTag(json.spawn.biomes) : json.spawn?.biomes ?? []
  return {
    id: `ancientcreature:${id}`,
    name: lang[`species.ancientcreature.${id}`] ?? title(id),
    category: json.entity_category ?? 'land',
    profile: (json.behavior?.profile ?? '').replace(/^ancientcreature:/, ''),
    health: json.attributes?.max_health ?? 0,
    attack: json.attributes?.attack_damage ?? 0,
    speed: json.attributes?.movement_speed ?? json.attributes?.flying_speed ?? 0,
    armor: json.attributes?.armor ?? 0,
    width: json.physical?.width ?? 0,
    height: json.physical?.height ?? 0,
    adultAge: json.growth?.adult_age ?? 0,
    incubationTime: json.spawn?.incubation_time ?? 0,
    diet: [json.diet?.items ?? []].flat(),
    biomes,
  }
})

// Dig sites: each structure, the biomes it generates in, and what brushing its suspicious blocks gives.
const digSites = jsonFiles(join(data, 'worldgen/structure')).map((file) => {
  const id = basename(file, '.json')
  return { id, name: title(id), biomes: biomeTag(`#ancientcreature:has_structure/${id}`) }
})
const archaeology = Object.fromEntries(
  jsonFiles(join(data, 'loot_table/archaeology')).map((file) => {
    const pool = readJson(join(data, 'loot_table/archaeology', file)).pools[0]
    const entries = pool.entries.map((entry) => ({
      item: entry.type === 'minecraft:empty' ? null : entry.name,
      weight: entry.weight ?? 1,
      parts: entry.modifier?.fossilParts?.map((part) => part.replace(/^ancientcreature:/, '')) ?? null,
    }))
    return [basename(file, '.json'), entries]
  }),
)

mkdirSync(join(docs, '.vitepress/data'), { recursive: true })
writeFileSync(
  join(docs, '.vitepress/data/data.json'),
  JSON.stringify({ names, textures, recipes, advancements, fossilParts, chisels, dnaLevels, config, species, digSites, archaeology }, null, 2),
)
console.log(
  `Synced ${recipes.length} recipes, ${advancements.length} advancements, ${fossilParts.length} fossil parts, ${chisels.length} chisels, ` +
    `${species.length} species, ${digSites.length} dig sites, ${Object.keys(textures).length} textures.`,
)
