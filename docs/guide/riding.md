# Riding creatures

The player who owns a creature can saddle it, armor it and ride it once it is an adult. This works for every species, including species added by datapacks.

The owner is the player who released the creature from a capsule, or the nearest player when its egg hatched. Other players can't mount it, equip it or control it.

## Creature Saddle

<RecipeCard id="creature_saddle" />

Use a **Creature Saddle** on your adult creature to saddle it. A creature needs a saddle before it can be ridden, unless `Riding.requireSaddle` is turned off in the [config](./compat#config).

Thick Hide drops from most large creatures. See [Creature materials](./items#creature-materials).

## Creature armor

<div class="ac-recipes">
<RecipeCard id="iron_creature_armor" />
<RecipeCard id="golden_creature_armor" />
<RecipeCard id="diamond_creature_armor" />
<RecipeCard id="netherite_creature_armor_smithing" />
</div>

Armor fits any species. Use it on your adult creature to put it on, and it shows on the creature's model.

| Armor | Armor points | Toughness | Knockback resistance |
| --- | --- | --- | --- |
| Iron | +6 | | |
| Golden | +8 | | |
| Diamond | +12 | +2 | |
| Netherite | +15 | +3 | +20% |

Netherite armor is made from diamond armor at a smithing table, like any netherite gear, and doesn't burn.

**Sneak-use with an empty hand** to take gear off: the armor first, then the saddle. Gear drops when the creature dies.

## Mounting

**Right-click** your saddled adult creature with an empty hand, or with anything that isn't food or gear. A creature can't be mounted while someone is already riding it.

Food comes first, so right-clicking with food feeds or breeds the creature instead. Sneak-right-click never mounts.

## Controls

| Input | Action |
| --- | --- |
| <kbd>W</kbd> / <kbd>S</kbd> | Move forward / backward (backward is slow) |
| <kbd>A</kbd> / <kbd>D</kbd> | Strafe left / right |
| Mouse | Set the creature's facing direction |
| <kbd>Space</kbd> | Gain extra height while riding a flying creature |
| <kbd>R</kbd> | Use the creature's mount ability |
| <kbd>Shift</kbd> | Dismount |

Flying and aquatic creatures follow the rider's view: look up to climb and down to descend. Land creatures stay on the ground and use their species' movement speed.

## Mount abilities

Press <kbd>R</kbd> (rebindable as *Mount Ability*) while riding to use the creature's special move. Each species has one ability with its own cooldown; the [Creatures](./creatures) page lists them. Abilities never hit you, your other creatures, or anything you couldn't attack anyway, such as other players on a no-PvP server.

| Ability | What it does |
| --- | --- |
| **Roar** | A terrifying call: nearby hostile mobs are weakened, slowed and run away |
| **Charge** | A short sprint that rams and throws everything ahead, smashing weak blocks in the way |
| **Bite** | A crushing bite on the closest creature directly ahead, slowing it |
| **Tail sweep** | A sweep of the tail that hits and knocks back everything behind and beside |
| **Stomp** | A ground-shaking stomp that hurts and slows everything standing close by |
| **Pounce** | A leap forward that strikes whatever it lands on |
| **Dive** | Flyers swoop steeply and strike what's below and ahead; swimmers get a burst of speed that rams what's in front |

Damage scales with the creature's attack strength. If the ability is still recharging, the remaining time shows above the hotbar.

## Restrictions

* Only the creature's owner can mount it.
* Baby creatures can't be ridden or equipped.
* Only one rider is supported at a time.
