import { defineConfig } from 'vitepress'

// GitHub Pages serves a project site from /<repository>/. For a custom domain or a user site, build with DOCS_BASE=/.
const base = process.env.DOCS_BASE ?? '/Ancient-Creature/'
const ICON = 'https://storage.googleapis.com/coolerpromc/textures/ancientcreature/fossil_part_skull.png'

export default defineConfig({
  title: 'Ancient Creature',
  description: 'Dig up fossils, extract their DNA and bring prehistoric creatures back to life in Minecraft. Add your own species with a datapack.',
  lang: 'en-US',
  base,
  cleanUrls: true,
  lastUpdated: true,
  srcExclude: ['README.md', 'scripts/**'],
  head: [
    ['link', { rel: 'icon', type: 'image/png', href: ICON }],
    ['meta', { name: 'theme-color', content: '#9a5b1c' }],
  ],
  themeConfig: {
    logo: { src: ICON, alt: '' },
    nav: [
      { text: 'Guide', link: '/guide/getting-started', activeMatch: '/guide/' },
      { text: 'Creatures', link: '/guide/creatures' },
      { text: 'Custom species', link: '/species/', activeMatch: '/species/' },
    ],
    sidebar: [
      {
        text: 'Guide',
        items: [
          { text: 'Getting started', link: '/guide/getting-started' },
          { text: 'Finding fossils', link: '/guide/fossils' },
          { text: 'Cleaning & identifying', link: '/guide/cleaning-and-identifying' },
          { text: 'DNA & genomes', link: '/guide/dna-and-genomes' },
          { text: 'Reviving a creature', link: '/guide/reviving' },
          { text: 'Creatures', link: '/guide/creatures' },
          { text: 'Riding creatures', link: '/guide/riding' },
          { text: 'Items & recipes', link: '/guide/items' },
          { text: 'Advancements', link: '/guide/advancements' },
          { text: 'JEI, Jade & config', link: '/guide/compat' },
        ],
      },
      {
        text: 'Custom species',
        collapsed: false,
        items: [
          { text: 'Overview', link: '/species/' },
          { text: 'Add a species', link: '/species/quick-start' },
          { text: 'Testing a species', link: '/species/testing' },
          {
            text: 'Data formats',
            items: [
              { text: 'Species (datapack)', link: '/species/species-json' },
              { text: 'Species (resource pack)', link: '/species/client-species' },
              { text: 'Behaviour components', link: '/species/behaviors' },
            ],
          },
          {
            text: 'Models & animation',
            items: [
              { text: 'Bedrock geometry', link: '/species/geometry' },
              { text: 'Bedrock animations', link: '/species/animations' },
              { text: 'Animation controllers', link: '/species/animation-controllers' },
            ],
          },
          {
            text: 'Reference',
            items: [
              { text: 'Minimal example', link: '/species/example' },
              { text: 'Commands', link: '/species/commands' },
              { text: 'API for mod developers', link: '/species/api' },
              { text: 'Migration & compatibility', link: '/species/migration' },
              { text: 'Limitations', link: '/species/limitations' },
            ],
          },
        ],
      },
    ],
    search: { provider: 'local' },
    outline: { level: [2, 3] },
    socialLinks: [{ icon: 'github', link: 'https://github.com/CoolerProYT/Ancient-Creature' }],
    footer: {
      message: 'Ancient Creature is in beta; species formats may change between releases.',
    },
  },
})
