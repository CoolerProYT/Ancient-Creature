Add-Type -AssemblyName System.Drawing

$textureDirectory = Resolve-Path "$PSScriptRoot\..\..\common\src\main\resources\assets\ancientcreature\textures\block"

function Write-PixelTexture {
    param(
        [string]$Name,
        [hashtable]$Palette,
        [string[]]$Rows
    )

    if ($Rows.Count -ne 16 -or ($Rows | Where-Object Length -ne 16)) {
        throw "$Name must be a 16x16 character map."
    }

    $bitmap = [System.Drawing.Bitmap]::new(16, 16)
    try {
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $key = [string]$Rows[$y][$x]
                if (-not $Palette.ContainsKey($key)) {
                    throw "Unknown palette key '$key' in $Name at $x,$y."
                }
                $bitmap.SetPixel($x, $y, [System.Drawing.ColorTranslator]::FromHtml($Palette[$key]))
            }
        }

        $path = Join-Path $textureDirectory "$Name.png"
        $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $bitmap.Dispose()
    }
}

Write-PixelTexture -Name "rock_pile_stone" -Palette @{
    "0" = "#343A37"
    "1" = "#454C47"
    "2" = "#59605A"
    "3" = "#6D736A"
    "4" = "#83877A"
    "5" = "#9A9B88"
    "a" = "#465337"
    "b" = "#617047"
    "c" = "#82905B"
} -Rows @(
    "3344333222334443"
    "3445543322234433"
    "3344432211233332"
    "2233321101222333"
    "2233332222333444"
    "3344443333344554"
    "3344333222234443"
    "2233222111233332"
    "3332222333444332"
    "4432223344554433"
    "4432112344443333"
    "3321122333322223"
    "332223344332aabb"
    "44333445543abbcc"
    "54444443332abccb"
    "44333222333aabbb"
)

Write-PixelTexture -Name "dragon_egg_fossil" -Palette @{
    "0" = "#4A4031"
    "1" = "#6B5D45"
    "2" = "#8E7D5B"
    "3" = "#B0A073"
    "4" = "#CCBD8D"
    "5" = "#E3D6A8"
    "r" = "#92613E"
} -Rows @(
    "3444554433332233"
    "4455554433222234"
    "3444432rr2112344"
    "3333222111233444"
    "2233332223344554"
    "2344443334455543"
    "3344333223344433"
    "3332211123333332"
    "4422r22233444333"
    "4331rr3344554433"
    "3322333444433222"
    "2233444433222233"
    "3344554332233444"
    "4455443222344554"
    "3444332212334443"
    "3333221122333333"
)

Write-PixelTexture -Name "dragon_egg_fossil_crack" -Palette @{
    "0" = "#2C2925"
    "1" = "#3D3830"
    "2" = "#554B3D"
} -Rows @(
    "0100010001000100"
    "1000100010001000"
    "0010001000100010"
    "0100010001000100"
    "1000100010001000"
    "0010001000100010"
    "0100010001000100"
    "1000100010001000"
    "0010001000100010"
    "0100010001000100"
    "1000100010001000"
    "0010001000100010"
    "0100010001000100"
    "1000100010001000"
    "0010001000100010"
    "0100010001000100"
)

Write-PixelTexture -Name "buried_fossil_fragment" -Palette @{
    "0" = "#4A4439"
    "1" = "#6B604B"
    "2" = "#8E7D5A"
    "3" = "#B09D72"
    "4" = "#CCBC8E"
    "5" = "#E1D4A8"
    "p" = "#746D5C"
    "r" = "#895E42"
} -Rows @(
    "3344554332212333"
    "3455443321123443"
    "2344432211233444"
    "1233321122344554"
    "2234332233444433"
    "3345443344333222"
    "3454432233221123"
    "2343321122332234"
    "1233232233443344"
    "2334343344554433"
    "3445454433443322"
    "4554343322332211"
    "34432322p3221233"
    "2332122pp4332344"
    "322123pprr344433"
    "4332344rr4333222"
)
