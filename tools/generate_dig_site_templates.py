"""
Builds the dig site structure templates (common/src/main/resources/data/ancientcreature/structure).

For each of the nine dig site biomes it:
  * updates the camp (<type>_dig_site.nbt): biome-specific brushing loot, a little fossil ore in
    the main pit floor, and three jigsaw connectors (north, east, south) that grow excavation pits;
  * writes dig_site/<type>/abandoned_camp.nbt, a ruined copy of the camp with cobwebs, torn tents
    and a spawner of undead archaeologists in the main pit;
  * writes dig_site/<type>/{trench,quarry,skeleton,sinkhole}.nbt, the excavation pits.

Safe to re-run: connectors already in a camp are turned back into their original blocks first.
Requires nbtlib (pip install nbtlib).
"""
import math
import os
import random

import nbtlib
from nbtlib.tag import Byte, Compound, Int, IntArray, List, String, Float, Short

ROOT = os.path.join(os.path.dirname(__file__), "..", "common", "src", "main", "resources", "data", "ancientcreature", "structure")
MODID = "ancientcreature"
DATA_VERSION = 4786
GROUND = 6  # template y of the camp's ground level; pits share it

PALETTES = {
    "plains":   dict(top="minecraft:grass_block[snowy=false]", fill="minecraft:dirt", rock="minecraft:tuff", loose="minecraft:gravel", accent="minecraft:coarse_dirt", wood="oak", planks="minecraft:spruce_planks", wool="minecraft:yellow_wool", mob="minecraft:zombie"),
    "arid":     dict(top="minecraft:sand", fill="minecraft:sandstone", rock="minecraft:smooth_sandstone", loose="minecraft:sand", accent="minecraft:cut_sandstone", wood="acacia", planks="minecraft:acacia_planks", wool="minecraft:orange_wool", mob="minecraft:husk"),
    "savanna":  dict(top="minecraft:grass_block[snowy=false]", fill="minecraft:coarse_dirt", rock="minecraft:orange_terracotta", loose="minecraft:gravel", accent="minecraft:terracotta", wood="acacia", planks="minecraft:acacia_planks", wool="minecraft:orange_wool", mob="minecraft:husk"),
    "badlands": dict(top="minecraft:red_sand", fill="minecraft:terracotta", rock="minecraft:red_terracotta", loose="minecraft:gravel", accent="minecraft:orange_terracotta", wood="acacia", planks="minecraft:acacia_planks", wool="minecraft:orange_wool", mob="minecraft:husk"),
    "jungle":   dict(top="minecraft:grass_block[snowy=false]", fill="minecraft:mud", rock="minecraft:packed_mud", loose="minecraft:gravel", accent="minecraft:rooted_dirt", wood="jungle", planks="minecraft:jungle_planks", wool="minecraft:lime_wool", mob="minecraft:zombie"),
    "cold":     dict(top="minecraft:snow_block", fill="minecraft:dirt", rock="minecraft:stone", loose="minecraft:gravel", accent="minecraft:packed_ice", wood="spruce", planks="minecraft:spruce_planks", wool="minecraft:light_blue_wool", mob="minecraft:stray"),
    "swamp":    dict(top="minecraft:grass_block[snowy=false]", fill="minecraft:mud", rock="minecraft:packed_mud", loose="minecraft:gravel", accent="minecraft:clay", wood="mangrove", planks="minecraft:mangrove_planks", wool="minecraft:green_wool", mob="minecraft:bogged"),
    "highland": dict(top="minecraft:grass_block[snowy=false]", fill="minecraft:stone", rock="minecraft:andesite", loose="minecraft:gravel", accent="minecraft:cobblestone", wood="spruce", planks="minecraft:spruce_planks", wool="minecraft:gray_wool", mob="minecraft:zombie"),
    "coastal":  dict(top="minecraft:sand", fill="minecraft:sandstone", rock="minecraft:stone", loose="minecraft:sand", accent="minecraft:clay", wood="oak", planks="minecraft:spruce_planks", wool="minecraft:cyan_wool", mob="minecraft:drowned"),
}
SANDY = {"arid", "coastal"}
PITS = ["trench", "quarry", "skeleton", "sinkhole"]
FOSSIL_ORE = MODID + ":fossil_ore"


def parse_state(state):
    """'minecraft:x[a=b,c=d]' -> (name, {a: b, c: d})"""
    if "[" not in state:
        return state, {}
    name, props = state[:-1].split("[", 1)
    return name, dict(p.split("=", 1) for p in props.split(","))


class Template:
    def __init__(self, sx, sy, sz):
        self.size = (sx, sy, sz)
        self.blocks = {}  # (x, y, z) -> (state string, nbt dict or None)

    def set(self, x, y, z, state, nbt=None):
        sx, sy, sz = self.size
        if 0 <= x < sx and 0 <= y < sy and 0 <= z < sz:
            self.blocks[(x, y, z)] = (state, nbt)

    def get(self, x, y, z):
        entry = self.blocks.get((x, y, z))
        return entry[0] if entry else None

    def clear(self, x, y, z):
        self.blocks.pop((x, y, z), None)

    def save(self, path):
        palette, index = [], {}
        blocks = []
        for (x, y, z), (state, nbt) in sorted(self.blocks.items(), key=lambda e: (e[0][1], e[0][2], e[0][0])):
            if state not in index:
                index[state] = len(palette)
                name, props = parse_state(state)
                entry = Compound({"Name": String(name)})
                if props:
                    entry["Properties"] = Compound({k: String(v) for k, v in props.items()})
                palette.append(entry)
            block = Compound({"pos": List[Int]([Int(x), Int(y), Int(z)]), "state": Int(index[state])})
            if nbt is not None:
                block["nbt"] = nbt
            blocks.append(block)
        root = Compound({
            "size": List[Int]([Int(v) for v in self.size]),
            "entities": List[Compound]([]),
            "blocks": List[Compound](blocks),
            "palette": List[Compound](palette),
            "DataVersion": Int(DATA_VERSION),
        })
        os.makedirs(os.path.dirname(path), exist_ok=True)
        nbtlib.File(root).save(path, gzipped=True)

    @staticmethod
    def load(path):
        f = nbtlib.load(path)
        t = Template(*[int(v) for v in f["size"]])
        states = []
        for p in f["palette"]:
            name = str(p["Name"])
            props = p.get("Properties")
            if props:
                name += "[" + ",".join(f"{k}={v}" for k, v in props.items()) + "]"
            states.append(name)
        for b in f["blocks"]:
            x, y, z = (int(v) for v in b["pos"])
            t.blocks[(x, y, z)] = (states[int(b["state"])], b.get("nbt"))
        return t


def brushable(kind, loose_name):
    block = "minecraft:suspicious_sand" if kind in SANDY else "minecraft:suspicious_gravel"
    nbt = Compound({
        "id": String("minecraft:brushable_block"),
        "LootTable": String(f"{MODID}:archaeology/{kind}_dig_site"),
        "components": Compound({}),
    })
    return block + "[dusted=0]", nbt


def jigsaw(facing, name, target, pool, final_state):
    state = f"minecraft:jigsaw[orientation={facing}_up]"
    nbt = Compound({
        "id": String("minecraft:jigsaw"),
        "name": String(name),
        "target": String(target),
        "pool": String(pool),
        "final_state": String(final_state),
        "joint": String("rollable"),
        "placement_priority": Int(0),
        "selection_priority": Int(0),
        "components": Compound({}),
    })
    return state, nbt


def fence(wood):
    return f"minecraft:{wood}_fence[east=false,north=false,south=false,waterlogged=false,west=false]"


def post_with_flag(t, x, z, p, rng, lantern=False):
    t.set(x, GROUND + 1, z, fence(p["wood"]))
    t.set(x, GROUND + 2, z, "minecraft:lantern[hanging=false,waterlogged=false]" if lantern else p["wool"])


def wall_column(t, x, z, p, floor_y, rng):
    """Strata of an excavation wall, from the surface down to just below the floor."""
    for y in range(floor_y - 1, GROUND + 1):
        if y == GROUND:
            state = p["accent"]
        elif y == GROUND - 2:
            state = p["loose"]  # the brushable band: the processor turns some of it suspicious
        elif y <= floor_y:
            state = FOSSIL_ORE if rng.random() < 0.12 else p["rock"]
        else:
            state = p["fill"]
        t.set(x, y, z, state)


def place_back_connector(t, p):
    """The pit's own jigsaw: middle of its south edge, facing back toward the camp."""
    sx, _, sz = t.size
    x, z = sx // 2, sz - 1
    final = t.get(x, GROUND, z) or "minecraft:structure_void"
    state, nbt = jigsaw("south", f"{MODID}:pit_back", "minecraft:empty", "minecraft:empty", final)
    t.set(x, GROUND, z, state, nbt)


def pit_trench(kind, p, rng):
    t = Template(9, 13, 13)
    hole = lambda x, z: 3 <= x <= 5 and 2 <= z <= 10
    floor = 2
    for x in range(1, 8):
        for z in range(1, 12):
            if hole(x, z):
                for y in range(floor + 1, GROUND + 3):
                    t.set(x, y, z, "minecraft:air")
                t.set(x, floor, z, FOSSIL_ORE if rng.random() < 0.1 else p["rock"])
                t.set(x, floor - 1, z, p["rock"])
                if rng.random() < 0.35:
                    t.set(x, floor + 1, z, p["loose"])  # spoil heaps on the trench floor
            elif 2 <= x <= 6 and 1 <= z <= 11:
                wall_column(t, x, z, p, floor, rng)
    for (x, z) in rng.sample([(x, z) for x in range(3, 6) for z in range(2, 11)], 4):
        t.set(x, floor + 1, z, *brushable(kind, p["loose"]))
    for y in range(floor + 1, GROUND + 1):
        t.set(4, y, 10, "minecraft:scaffolding[bottom=false,distance=0,waterlogged=false]")
    for (x, z) in [(1, 1), (7, 1), (1, 6), (7, 6), (1, 11), (7, 11)]:
        post_with_flag(t, x, z, p, rng, lantern=(z == 6))
    t.set(6, GROUND + 1, 11, "minecraft:barrel[facing=up,open=false]", Compound({"id": String("minecraft:barrel"), "Items": List[Compound]([]), "components": Compound({})}))
    place_back_connector(t, p)
    return t


def pit_quarry(kind, p, rng):
    t = Template(11, 13, 11)
    ring = lambda x, z: max(abs(x - 5), abs(z - 5))
    for x in range(11):
        for z in range(11):
            r = ring(x, z)
            if r == 5:
                wall_column(t, x, z, p, 0, rng)
                continue
            floor = GROUND - 1 if r == 4 or r == 3 else (GROUND - 3 if r == 2 else 0)
            for y in range(floor + 1, GROUND + 3):
                t.set(x, y, z, "minecraft:air")
            t.set(x, floor, z, p["loose"] if floor > 0 else (FOSSIL_ORE if rng.random() < 0.2 else p["rock"]))
            for y in range(0, floor):
                band = GROUND - 2 - y
                t.set(x, y, z, p["loose"] if band == 0 else (FOSSIL_ORE if y < 2 and rng.random() < 0.1 else (p["fill"] if y > 2 else p["rock"])))
    terrace = [(x, z) for x in range(11) for z in range(11) if ring(x, z) == 2]
    for (x, z) in rng.sample(terrace, 2):
        t.set(x, GROUND - 3, z, *brushable(kind, p["loose"]))
    bottom = [(x, z) for x in range(11) for z in range(11) if ring(x, z) <= 1]
    for (x, z) in rng.sample(bottom, 3):
        t.set(x, 0, z, *brushable(kind, p["loose"]))
    for y in range(1, GROUND + 1):
        t.set(4, y, 4, "minecraft:scaffolding[bottom=false,distance=0,waterlogged=false]")
    for (x, z) in [(0, 0), (10, 0), (0, 10), (10, 10)]:
        post_with_flag(t, x, z, p, rng, lantern=(x == 10 and z == 0))
    place_back_connector(t, p)
    return t


def pit_skeleton(kind, p, rng):
    t = Template(13, 13, 9)
    floor = GROUND - 2
    for x in range(13):
        for z in range(9):
            if x in (0, 12) or z in (0, 8):
                wall_column(t, x, z, p, floor, rng)
                continue
            for y in range(floor + 1, GROUND + 3):
                t.set(x, y, z, "minecraft:air")
            t.set(x, floor, z, p["loose"])
            t.set(x, floor - 1, z, FOSSIL_ORE if rng.random() < 0.08 else p["rock"])
    # A half-uncovered skeleton: spine along x, ribs arching over it, the skull at the east end.
    for x in range(2, 10):
        t.set(x, floor, 4, "minecraft:bone_block[axis=x]")
    for x in (4, 6, 8):
        for z in (2, 3, 5, 6):
            t.set(x, floor, z, "minecraft:bone_block[axis=z]")
        t.set(x, floor + 1, 3, "minecraft:bone_block[axis=z]")
        t.set(x, floor + 1, 5, "minecraft:bone_block[axis=z]")
    for x in (10, 11):
        for z in (3, 4, 5):
            t.set(x, floor, z, "minecraft:bone_block[axis=y]")
    t.set(10, floor + 1, 4, "minecraft:bone_block[axis=y]")
    spots = [(x, z) for x in range(1, 12) for z in range(1, 8) if t.get(x, floor, z) == p["loose"]]
    for (x, z) in rng.sample(spots, 5):
        t.set(x, floor, z, *brushable(kind, p["loose"]))
    for (x, z) in [(0, 0), (6, 0), (12, 0), (0, 8), (12, 8)]:
        post_with_flag(t, x, z, p, rng, lantern=(x == 6))
    place_back_connector(t, p)
    return t


def pit_sinkhole(kind, p, rng):
    t = Template(11, 13, 11)
    cx = cz = 5
    for x in range(11):
        for z in range(11):
            d = math.hypot(x - cx, z - cz)
            if d <= 3.5:
                for y in range(1, GROUND + 3):
                    t.set(x, y, z, "minecraft:air")
                t.set(x, 0, z, p["loose"])
            elif d <= 4.6:
                # Undercut: the cavity is wider at the bottom than the opening.
                for y in range(1, 3):
                    t.set(x, y, z, "minecraft:air")
                t.set(x, 0, z, p["loose"])
                for y in range(3, GROUND + 1):
                    t.set(x, y, z, p["accent"] if y == GROUND else (p["loose"] if y == GROUND - 2 else p["fill"]))
            elif d <= 5.6:
                for y in range(0, 4):
                    t.set(x, y, z, FOSSIL_ORE if 1 <= y <= 2 and rng.random() < 0.25 else p["rock"])
    floor_cells = [(x, z) for x in range(11) for z in range(11) if math.hypot(x - cx, z - cz) <= 4.6]
    for (x, z) in rng.sample(floor_cells, 4):
        t.set(x, 0, z, *brushable(kind, p["loose"]))
    cavity = [(x, z) for x in range(11) for z in range(11) if 3.5 < math.hypot(x - cx, z - cz) <= 4.6]
    for (x, z) in rng.sample(cavity, 5):
        t.set(x, 2, z, "minecraft:cobweb")
    for y in range(1, GROUND + 1):
        t.set(5, y, 2, "minecraft:scaffolding[bottom=false,distance=0,waterlogged=false]")
    for (x, z) in [(1, 1), (9, 1), (1, 9), (9, 9)]:
        post_with_flag(t, x, z, p, rng, lantern=(x == 9 and z == 1))
    place_back_connector(t, p)
    return t


PIT_BUILDERS = {"trench": pit_trench, "quarry": pit_quarry, "skeleton": pit_skeleton, "sinkhole": pit_sinkhole}

CAMP_CONNECTORS = [((12, GROUND, 0), "north"), ((24, GROUND, 10), "east"), ((12, GROUND, 20), "south")]


def normalise_camp(t):
    """Undo a previous run: jigsaws back to the blocks they stand in for."""
    for pos, (state, nbt) in list(t.blocks.items()):
        if state.startswith("minecraft:jigsaw"):
            final = str(nbt["final_state"]) if nbt is not None and "final_state" in nbt else "minecraft:structure_void"
            if final == "minecraft:structure_void":
                t.clear(*pos)
            else:
                t.set(*pos, final)


def update_camp(kind, p, t, rng):
    normalise_camp(t)
    for pos, (state, nbt) in list(t.blocks.items()):
        if state.startswith("minecraft:suspicious_"):
            t.set(*pos, *brushable(kind, p["loose"]))
    # A little fossil ore showing in the floor of the main pit.
    floor = [pos for pos, (state, _) in t.blocks.items() if pos[1] == 2 and 10 <= pos[0] <= 18 and 8 <= pos[2] <= 14 and not state.startswith("minecraft:air")]
    if not any(state == FOSSIL_ORE for state, _ in t.blocks.values()):
        for pos in rng.sample(sorted(floor), 3):
            t.set(*pos, FOSSIL_ORE)
    for pos, facing in CAMP_CONNECTORS:
        final = t.get(*pos) or "minecraft:structure_void"
        t.set(*pos, *jigsaw(facing, f"{MODID}:camp_edge", f"{MODID}:pit_back", f"{MODID}:dig_site/{kind}/pits", final))


def abandon(kind, p, camp, rng):
    t = Template(*camp.size)
    t.blocks = dict(camp.blocks)
    for pos, (state, nbt) in list(t.blocks.items()):
        name, props = parse_state(state)
        if name.endswith("_wool"):
            roll = rng.random()
            if roll < 0.35:
                t.set(*pos, "minecraft:air")
            elif roll < 0.5:
                t.set(*pos, "minecraft:cobweb")
        elif name.endswith("_carpet") and rng.random() < 0.5:
            t.set(*pos, "minecraft:air")
        elif name == "minecraft:scaffolding" and rng.random() < 0.4:
            t.set(*pos, "minecraft:air")
        elif name == "minecraft:lantern" and rng.random() < 0.6:
            t.set(*pos, "minecraft:air")
        elif name.endswith("_fence") and pos[1] > GROUND and rng.random() < 0.15:
            t.set(*pos, "minecraft:air")
        elif name == "minecraft:chest":
            new = Compound(nbt)
            new["LootTable"] = String(f"{MODID}:chests/abandoned_dig_site")
            t.set(*pos, state, new)
        elif name == "minecraft:dirt_path" and rng.random() < 0.3:
            t.set(*pos, "minecraft:coarse_dirt")
    # Cobwebs strung across the open air of the camp.
    air = [pos for pos, (state, _) in t.blocks.items() if state == "minecraft:air" and GROUND < pos[1] <= GROUND + 4]
    for pos in rng.sample(air, min(14, len(air))):
        t.set(*pos, "minecraft:cobweb")
    # The undead crew, spawning from the bottom of the main pit.
    spawn = Compound({
        "entity": Compound({"id": String(p["mob"])}),
        "equipment": Compound({"loot_table": String(f"{MODID}:equipment/archaeologist"), "slot_drop_chances": Float(0.1)}),
    })
    spawner = Compound({
        "id": String("minecraft:mob_spawner"),
        "SpawnData": spawn,
        "SpawnPotentials": List[Compound]([Compound({"weight": Int(1), "data": spawn})]),
        "Delay": Short(20),
        "MinSpawnDelay": Short(200),
        "MaxSpawnDelay": Short(600),
        "SpawnCount": Short(2),
        "MaxNearbyEntities": Short(4),
        "RequiredPlayerRange": Short(16),
        "SpawnRange": Short(3),
        "components": Compound({}),
    })
    t.set(14, 3, 12, "minecraft:spawner", spawner)
    return t


def main():
    for kind, p in PALETTES.items():
        rng = random.Random(sum(map(ord, kind)) * 7919)
        camp_path = os.path.join(ROOT, f"{kind}_dig_site.nbt")
        camp = Template.load(camp_path)
        update_camp(kind, p, camp, rng)
        camp.save(camp_path)
        abandon(kind, p, camp, rng).save(os.path.join(ROOT, "dig_site", kind, "abandoned_camp.nbt"))
        for pit in PITS:
            PIT_BUILDERS[pit](kind, p, rng).save(os.path.join(ROOT, "dig_site", kind, f"{pit}.nbt"))
        print("built", kind)


if __name__ == "__main__":
    main()
