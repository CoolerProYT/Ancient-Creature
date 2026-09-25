// @ts-ignore
import raw from '../data/data.json'

export interface Recipe {
  id: string
  type: string
  result: { id: string; count: number }
  pattern?: string[]
  key?: Record<string, string>
  ingredients?: string[]
  ingredient?: string
  cookingTime?: number
  experience?: number
  template?: string
  base?: string
  addition?: string
}

export interface Advancement {
  id: string
  parent: string | null
  icon: string | null
  title: string
  description: string
  frame: 'task' | 'goal' | 'challenge'
}

export interface FossilPart {
  id: string
  name: string
  completenessMin: number
  completenessMax: number
  dnaBonus: number
  identifyFailChance: number
  damageRate: number
}

export interface Chisel {
  id: string
  material: string
  durability: number
  damageRate: number
}

export interface DnaLevel {
  id: string
  name: string
  genomeMin: number
  genomeMax: number
  scoreMin: number
  scoreMax: number
}

export interface ConfigValue {
  key: string
  type: 'int' | 'float'
  default: number
  comment: string
}

export interface Species {
  id: string
  name: string
  category: 'land' | 'aquatic' | 'flying'
  profile: string
  health: number
  attack: number
  speed: number
  armor: number
  width: number
  height: number
  adultAge: number
  incubationTime: number
  diet: string[]
  biomes: string[]
}

export interface DigSite {
  id: string
  name: string
  biomes: string[]
}

export interface ArchaeologyEntry {
  item: string | null
  weight: number
  parts: string[] | null
}

export const data = raw as unknown as {
  names: Record<string, string>
  textures: Record<string, string>
  recipes: Recipe[]
  advancements: Advancement[]
  fossilParts: FossilPart[]
  chisels: Chisel[]
  dnaLevels: DnaLevel[]
  config: ConfigValue[]
  species: Species[]
  digSites: DigSite[]
  archaeology: Record<string, ArchaeologyEntry[]>
}

const TAG_NAMES: Record<string, string> = {
  '#minecraft:planks': 'Any Planks',
  '#minecraft:slabs': 'Any Slab',
  '#minecraft:meat': 'Any Meat',
  '#minecraft:stone_crafting_materials': 'Cobblestone (or Blackstone, Cobbled Deepslate)',
  '#minecraft:copper_tool_materials': 'Copper Ingot',
  '#minecraft:iron_tool_materials': 'Iron Ingot',
  '#minecraft:gold_tool_materials': 'Gold Ingot',
  '#minecraft:diamond_tool_materials': 'Diamond',
  'minecraft:potion': 'Potion (any, e.g. Water Bottle)',
}

/** Item shown for a tag ingredient, or for an item whose hosted render lives under another name. */
const ICON_ALIASES: Record<string, string> = {
  '#minecraft:planks': 'minecraft:oak_planks',
  '#minecraft:slabs': 'minecraft:oak_slab',
  '#minecraft:meat': 'minecraft:beef',
  '#minecraft:stone_crafting_materials': 'minecraft:cobblestone',
  '#minecraft:copper_tool_materials': 'minecraft:copper_ingot',
  '#minecraft:iron_tool_materials': 'minecraft:iron_ingot',
  '#minecraft:gold_tool_materials': 'minecraft:gold_ingot',
  '#minecraft:diamond_tool_materials': 'minecraft:diamond',
  'minecraft:potion': 'minecraft:potion/water',
}

/** Mod items use their in-game name; vanilla ids are turned into readable names. */
export function itemName(id: string): string {
  if (data.names[id]) return data.names[id]
  if (TAG_NAMES[id]) return TAG_NAMES[id]
  return prettify(id)
}

export function prettify(id: string): string {
  const path = id.replace(/^#/, '').split(':').pop() ?? id
  return path
    .split('_') // @ts-ignore
    .map((word) => (['of', 'the', 'with'].includes(word) ? word : word.charAt(0).toUpperCase() + word.slice(1)))
    .join(' ')
}

/** Hosted renders of vanilla items, one PNG per item id. Mojang's textures are not bundled here. */
const VANILLA_ICONS = 'https://storage.googleapis.com/coolerpromc/textures'

/**
 * Where to load an item's icon from. Mod items use the hosted textures listed by the sync script;
 * vanilla items use the hosted renders.
 */
export function itemIcon(id: string): string | null {
  const itemId = ICON_ALIASES[id] ?? id
  if (data.textures[itemId]) return data.textures[itemId]
  // @ts-ignore
  const [namespace, path] = itemId.includes(':') ? itemId.split(':') : ['minecraft', itemId]
  if (namespace !== 'minecraft') return null
  return `${VANILLA_ICONS}/${namespace}/${path}.png`
}

export function percent(fraction: number, digits = 0): string {
  const value = Math.round(fraction * 100 * 10 ** digits) / 10 ** digits
  return `${value}%`
}

export function signedPercent(fraction: number): string {
  return fraction > 0 ? `+${percent(fraction)}` : percent(fraction)
}

export function seconds(ticks: number): string {
  const value = ticks / 20
  // @ts-ignore
  return Number.isInteger(value) ? `${value} s` : `${value.toFixed(1)} s`
}

/** Real-time minutes for longer timers such as growing up. */
export function duration(ticks: number): string {
  const minutes = ticks / 1200
  if (minutes < 1) return seconds(ticks)
  // @ts-ignore
  return `${Number.isInteger(minutes) ? minutes : minutes.toFixed(1)} min`
}

/** Hearts, the way the game's health bar shows them. */
export function hearts(health: number): string {
  const value = health / 2
  // @ts-ignore
  return Number.isInteger(value) ? `${value}` : value.toFixed(1)
}

export function fossilItem(part: string, dirty = false): string {
  return `ancientcreature:fossil_part/${dirty ? 'dirty_' : ''}${part}`
}

/**
 * Completeness of a freshly dug fossil: the part's roll plus 10% per Fortune level (capped at 100%),
 * then reduced by the chisel's damage rate.
 */
export function dugCompleteness(value: number, damageRate: number, fortune = 0): number {
  return Math.min(1, value + fortune * 0.1) * (1 - damageRate)
}

/** The DNA level a fossil of this completeness and part yields, or null when it is too far gone. */
export function dnaLevelFor(completeness: number, part: FossilPart): DnaLevel | null {
  const score = Math.max(0.01, completeness + part.dnaBonus)
  if (score < (data.dnaLevels[0]?.scoreMin ?? 0.05)) return null
  return data.dnaLevels.find((level) => score <= level.scoreMax) ?? data.dnaLevels[data.dnaLevels.length - 1]
}

export const CATEGORY_NAMES: Record<string, string> = {
  land: 'Land',
  aquatic: 'Aquatic',
  flying: 'Flying',
}

export function profileName(profile: string): string {
  return prettify(profile)
}

/** How each shipped behaviour profile acts, in a player's terms. */
export const PROFILE_BLURBS: Record<string, string> = {
  passive: 'Harmless. Runs when hurt.',
  defensive_herbivore: 'Peaceful until threatened, then bellows and charges.',
  armored_herbivore: 'Grazes in herds and swings back when attacked.',
  sauropod_browser: 'Gentle giant that eats from treetops. Hits back if hurt.',
  pack_predator: 'Hunts in packs when hungry.',
  apex_predator: 'Roars, then charges. Hunts players and animals when hungry.',
  aquatic_predator: 'Hunts anything in the water when hungry.',
  flying_passive: 'Flies and does not fight back.',
  flying_predator: 'Circles, then dives on its prey when hungry.',
}
