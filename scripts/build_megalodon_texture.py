"""Build the Megalodon entity atlas from a generated shark-skin material.

The atlas layout mirrors the box-UV offsets assigned in megalodon.bbmodel.
"""

from pathlib import Path
from PIL import Image, ImageDraw, ImageEnhance, ImageFilter


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "art/blockbench/textures/megalodon_skin_source.png"
OUTPUT = ROOT / "common/src/main/resources/assets/ancientcreature/textures/entity/megalodon.png"


def toned(source: Image.Image, size: tuple[int, int], tint: tuple[int, int, int], brightness: float) -> Image.Image:
    tile = source.resize(size, Image.Resampling.LANCZOS)
    tile = ImageEnhance.Contrast(tile).enhance(0.72)
    tile = ImageEnhance.Brightness(tile).enhance(brightness)
    color = Image.new("RGB", size, tint)
    return Image.blend(tile, color, 0.34)


def paste_skin(atlas: Image.Image, source: Image.Image, box: tuple[int, int, int, int], tint: tuple[int, int, int], brightness: float) -> None:
    x, y, width, height = box
    atlas.paste(toned(source, (width, height), tint, brightness), (x, y))


def main() -> None:
    source = Image.open(SOURCE).convert("RGB")
    atlas = Image.new("RGB", (256, 256), (39, 51, 57))

    # Individually packed box-UV islands. Shared islands are intentional for paired fins.
    dorsal = (46, 61, 69)
    crown = (34, 47, 55)
    flank = (68, 83, 88)
    fin = (50, 66, 73)
    belly = (151, 158, 151)

    regions = [
        ((0, 0, 92, 43), dorsal, 0.95),       # body_core
        ((96, 0, 82, 32), flank, 1.08),       # body_depth
        ((0, 48, 50, 19), dorsal, 0.94),      # shoulder_mass
        ((56, 48, 54, 27), crown, 0.90),      # head_back
        ((112, 48, 48, 18), crown, 0.92),     # head_front
        ((164, 48, 40, 14), flank, 0.98),     # snout
        ((0, 72, 46, 13), crown, 0.84),       # brow
        ((50, 72, 52, 17), belly, 1.34),      # lower_jaw
        ((104, 72, 42, 11), belly, 1.43),     # jaw_chin
        ((150, 72, 52, 24), flank, 1.01),     # tail_base
        ((0, 100, 38, 17), flank, 1.00),
        ((40, 100, 40, 19), flank, 0.98),
        ((82, 100, 28, 13), fin, 0.94),
        ((112, 100, 25, 14), fin, 0.92),
        ((140, 100, 20, 19), fin, 0.91),
        ((162, 100, 14, 15), fin, 0.89),
        ((178, 100, 18, 18), fin, 0.91),
        ((198, 100, 14, 15), fin, 0.89),
        ((0, 122, 64, 17), fin, 0.93),         # pectoral main pair
        ((68, 122, 36, 10), fin, 0.88),        # pectoral tips
        ((108, 122, 29, 20), crown, 0.86),     # dorsal fin
        ((140, 122, 18, 14), crown, 0.84),
        ((160, 122, 13, 10), crown, 0.80),
        ((0, 176, 74, 27), belly, 1.38),       # belly_keel
    ]
    for box, tint, brightness in regions:
        paste_skin(atlas, source, box, tint, brightness)

    draw = ImageDraw.Draw(atlas)

    # Soft lateral countershade transition and irregular flank markings.
    for y in range(144, 172):
        t = (y - 144) / 28
        color = tuple(round((1 - t) * flank[i] + t * belly[i]) for i in range(3))
        draw.line((0, y, 220, y), fill=color)
    flank_strip = toned(source, (220, 28), flank, 1.12).filter(ImageFilter.GaussianBlur(0.35))
    atlas.paste(Image.blend(atlas.crop((0, 144, 220, 172)), flank_strip, 0.38), (0, 144))

    # Reserved detail swatches used by the tiny eye/gill, mouth, and tooth cubes.
    draw.rectangle((224, 0, 255, 23), fill=(11, 15, 17))
    draw.ellipse((228, 3, 238, 13), fill=(3, 5, 6))
    draw.ellipse((231, 5, 234, 8), fill=(108, 119, 105))
    for x in (242, 247, 252):
        draw.rectangle((x, 2, min(x + 1, 255), 21), fill=(20, 25, 26))

    mouth = toned(source, (32, 32), (76, 28, 31), 0.72)
    atlas.paste(mouth, (224, 24))
    draw.line((224, 25, 255, 25), fill=(112, 47, 48), width=2)

    teeth = toned(source, (32, 32), (205, 197, 163), 1.62)
    atlas.paste(teeth, (224, 56))
    draw.line((224, 56, 255, 56), fill=(237, 229, 192), width=2)

    # Keep unused atlas space coherent instead of transparent or glaring checkerboard.
    filler = toned(source, (256, 80), flank, 0.98)
    atlas.paste(filler.crop((74, 0, 256, 80)), (74, 176))
    paste_skin(atlas, source, (0, 176, 74, 27), belly, 1.38)
    draw.rectangle((224, 0, 255, 23), fill=(11, 15, 17))
    draw.ellipse((228, 3, 238, 13), fill=(3, 5, 6))
    draw.ellipse((231, 5, 234, 8), fill=(108, 119, 105))
    for x in (242, 247, 252):
        draw.rectangle((x, 2, min(x + 1, 255), 21), fill=(20, 25, 26))
    atlas.paste(mouth, (224, 24))
    atlas.paste(teeth, (224, 56))

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    atlas.save(OUTPUT, optimize=True)


if __name__ == "__main__":
    main()
