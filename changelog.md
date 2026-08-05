## 26.1.2.3
- Fossil Part is now data-driven under `ancientcreature:fossil_part`
- Rock Pile will be generated in all overworld biome now
- Added simple Species index to Fossil Identification Chamber screen, it will be enhanced
- Fixed Rock Pile generated inside water not waterlogged
- Fixed `SetFossilPartFunction` always select from all parts instead of defined parts
- Renamed `fossil_fragment` to `fossil_part`
- Removed `egg_fossil` item, it's now a part of `fossil_part`
- Removed `SetFossilCompletenessFunction` and `SetFossilSpeciesFunction` loot function
- Renamed `SetFossilPartFunction` to `SetFossilDataFunction`

Make sure to convert all your existing `fossil_fragment` and `egg_fossil` to DNA Sample before updating