import DefaultTheme from 'vitepress/theme'
import type { Theme } from 'vitepress'
import AdvancementTree from './components/AdvancementTree.vue'
import ChiselTable from './components/ChiselTable.vue'
import ConfigTable from './components/ConfigTable.vue'
import DigSites from './components/DigSites.vue'
import DnaTable from './components/DnaTable.vue'
import FossilCalculator from './components/FossilCalculator.vue'
import FossilPartTable from './components/FossilPartTable.vue'
import ItemGallery from './components/ItemGallery.vue'
import ItemSlot from './components/ItemSlot.vue'
import MachineRecipe from './components/MachineRecipe.vue'
import ProcessChain from './components/ProcessChain.vue'
import RecipeCard from './components/RecipeCard.vue'
import SpeciesExplorer from './components/SpeciesExplorer.vue'
import './style.css'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('AdvancementTree', AdvancementTree)
    app.component('ChiselTable', ChiselTable)
    app.component('ConfigTable', ConfigTable)
    app.component('DigSites', DigSites)
    app.component('DnaTable', DnaTable)
    app.component('FossilCalculator', FossilCalculator)
    app.component('FossilPartTable', FossilPartTable)
    app.component('ItemGallery', ItemGallery)
    app.component('ItemSlot', ItemSlot)
    app.component('MachineRecipe', MachineRecipe)
    app.component('ProcessChain', ProcessChain)
    app.component('RecipeCard', RecipeCard)
    app.component('SpeciesExplorer', SpeciesExplorer)
  },
} satisfies Theme
