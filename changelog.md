## 26.1.2.1
- Exposed all block entity container instead of `null` when direction is `null` for Jade to lookup
- Ancient Creatures will lay egg (as block) instead of directly spawn baby when breeding (The egg will not drop anything if broken)
- Baby creature spawned from egg will treat the nearest player that is within distance of 10 as their owner
- Rewritten all data component type
- Added advancements

Note: If existing world contain ItemStack that using old components might crash