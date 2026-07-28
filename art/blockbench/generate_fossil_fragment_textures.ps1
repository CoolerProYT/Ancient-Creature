param(
    [string]$OutputDirectory = (Join-Path $PSScriptRoot '..\..\common\src\main\resources\assets\ancientcreature\textures\item\fossil_fragment')
)

Add-Type -AssemblyName System.Drawing

$palette = [System.Collections.Generic.Dictionary[string, System.Drawing.Color]]::new(
    [System.StringComparer]::Ordinal
)
$palette.Add('.', [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
$palette.Add('O', [System.Drawing.Color]::FromArgb(255, 60, 52, 42))
$palette.Add('s', [System.Drawing.Color]::FromArgb(255, 159, 142, 114))
$palette.Add('m', [System.Drawing.Color]::FromArgb(255, 204, 192, 171))
$palette.Add('h', [System.Drawing.Color]::FromArgb(255, 219, 213, 195))
$palette.Add('d', [System.Drawing.Color]::FromArgb(255, 86, 60, 43))
$palette.Add('D', [System.Drawing.Color]::FromArgb(255, 107, 61, 31))
$palette.Add('b', [System.Drawing.Color]::FromArgb(255, 86, 49, 25))
$palette.Add('B', [System.Drawing.Color]::FromArgb(255, 127, 73, 36))
$palette.Add('r', [System.Drawing.Color]::FromArgb(255, 84, 84, 84))

$cleanTextures = @{
    tooth = @(
        '...OOOOOO.......',
        '..OssssssO......',
        '..OmhhhhmO......',
        '...OmhhhmO......',
        '...OmhhmO.......',
        '....OmhmO.......',
        '....OmhO........',
        '.....OmO........',
        '.....OmO........',
        '......OO........',
        '......O.........',
        '................',
        '................',
        '................',
        '................',
        '................'
    )
    skull = @(
        '................',
        '..OOOOOOOO......',
        '.OssssssssOO....',
        '.OsmhhhmmmmsOO..',
        'OsmhOOOmmmssmO..',
        'OmhO...OmmmhhmO.',
        'OmO.OO.OmmhOOO..',
        'OmO.O..OmmmO....',
        'OsmO.OOOmmmsOO..',
        '.OmhhhhmmmsssO..',
        '..OOOOOmmOOOOO..',
        '......OmmO......',
        '......OssOOOO...',
        '.......OOOOO....',
        '................',
        '................'
    )
    vertebra = @(
        '.......O........',
        '......OsO.......',
        '......OmO.......',
        '..OOO.OmO.OOO...',
        '.OssOOmmmOOsO...',
        'OsmhmmOOmmhmsO..',
        '.OmmmO..OmmmmO..',
        '..OmmOOOOmmO....',
        '...OmhhhhmO.....',
        '..OOOmmmmOOO....',
        '.OssO.OmO.OssO..',
        '..OO..OmO..OO...',
        '......OsO.......',
        '.......O........',
        '................',
        '................'
    )
    limb = @(
        '..OOOO..........',
        '.OshmOO.........',
        '.OhmmmO.........',
        '..OmmO..........',
        '...OmO..........',
        '...OmO..........',
        '....OmO.........',
        '....OmO.........',
        '.....OmO........',
        '.....OmO........',
        '......OmO.......',
        '......OmmO......',
        '.....OhmmmO.....',
        '.....OmhhsO.....',
        '......OOOO......',
        '................'
    )
    claw = @(
        '..OOOOOO........',
        '.OssmmmmO.......',
        '.OmhhhhhmO......',
        '..OmmOOmmO......',
        '...OO..OmmO.....',
        '.......OhmO.....',
        '......OhmO......',
        '.....OhmO.......',
        '....OhmO........',
        '...OhmO.........',
        '..OhmO..........',
        '..OmO...........',
        '...OO...........',
        '....O...........',
        '................',
        '................'
    )
}

# Each stamp is "x,y,palette-key". Dirt is deliberately clustered around broad,
# broken ends while highlights remain visible, matching the existing dirty rib.
$dirtStamps = @{
    tooth = @(
        '2,0,d','3,0,D','6,0,b','7,0,B','8,1,d','1,1,b','2,1,B',
        '3,2,D','4,2,d','7,2,B','8,2,b','2,3,d','3,3,B','4,4,D',
        '2,4,b','3,5,d','4,5,r','5,6,D','6,6,b','4,7,B','5,7,d',
        '6,8,D','5,9,b','6,9,B','6,10,d','7,10,b'
    )
    skull = @(
        '2,1,d','3,1,D','7,1,b','8,1,B','1,2,b','2,2,B','4,2,d',
        '9,2,D','10,2,b','0,3,d','1,3,B','5,3,r','10,3,B','11,3,d',
        '0,4,b','1,4,D','2,4,d','7,4,B','8,4,b','13,4,d','0,5,B',
        '3,5,d','4,5,r','8,5,D','12,5,b','13,5,B','1,6,d','5,6,B',
        '7,6,b','8,6,d','11,6,D','0,7,b','1,7,B','6,7,d','9,7,B',
        '2,8,D','3,8,b','8,8,d','12,8,B','13,8,b','4,9,d','5,9,D',
        '9,9,b','10,9,B','6,10,d','7,10,b','11,10,D','7,11,B',
        '8,11,d','6,12,b','9,12,D','10,12,B','8,13,d'
    )
    vertebra = @(
        '6,0,d','7,0,B','8,1,b','5,2,D','6,2,d','2,3,b','3,3,B',
        '8,3,d','10,3,D','11,3,b','1,4,d','2,4,B','5,4,r','9,4,b',
        '12,4,D','0,5,b','1,5,B','4,5,d','10,5,B','13,5,d','2,6,D',
        '3,6,b','7,6,r','11,6,d','1,7,b','4,7,B','8,7,D','12,7,b',
        '3,8,d','5,8,D','9,8,b','10,8,B','2,9,B','6,9,d','7,9,r',
        '11,9,D','1,10,b','3,10,d','8,10,B','12,10,b','5,11,D',
        '6,11,b','9,11,d','6,12,B','7,12,d','7,13,b'
    )
    limb = @(
        '1,0,d','2,0,B','4,0,b','5,1,D','0,1,b','1,1,B','2,2,d',
        '4,2,B','5,2,b','1,3,D','2,3,d','3,4,B','4,4,b','2,5,d',
        '3,5,r','4,6,D','5,6,b','3,7,B','4,7,d','5,8,D','6,8,b',
        '4,9,d','5,9,r','6,10,B','7,10,b','5,11,D','6,11,d',
        '8,11,B','5,12,b','6,12,B','8,12,d','9,12,D','5,13,d',
        '7,13,b','9,13,B','6,14,D','8,14,b','9,14,d'
    )
    claw = @(
        '1,0,d','2,0,B','5,0,b','6,0,D','0,1,b','1,1,B','3,1,d',
        '7,1,B','8,1,b','1,2,d','2,2,D','5,2,r','7,2,b','2,3,B',
        '3,3,d','6,3,D','8,3,b','3,4,b','4,4,B','7,4,d','9,4,D',
        '7,5,b','8,5,B','9,5,d','6,6,D','7,6,d','5,7,b','6,7,B',
        '4,8,d','5,8,r','3,9,B','4,9,d','2,10,b','3,10,D','1,11,d',
        '2,11,B','3,12,b','4,12,d','3,13,D','4,13,b'
    )
}

function Write-PixelTexture {
    param(
        [string]$Path,
        [string[]]$Rows
    )

    if ($Rows.Count -ne 16 -or ($Rows | Where-Object { $_.Length -ne 16 })) {
        throw "Texture '$Path' must be exactly 16x16."
    }

    $bitmap = [System.Drawing.Bitmap]::new(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        for ($y = 0; $y -lt 16; $y++) {
            for ($x = 0; $x -lt 16; $x++) {
                $key = [string]$Rows[$y][$x]
                $bitmap.SetPixel($x, $y, $palette[$key])
            }
        }
        $bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    }
    finally {
        $bitmap.Dispose()
    }
}

function Add-Dirt {
    param(
        [string[]]$Rows,
        [string[]]$Stamps
    )

    $result = [object[]]::new($Rows.Count)
    for ($rowIndex = 0; $rowIndex -lt $Rows.Count; $rowIndex++) {
        $result[$rowIndex] = [char[]]$Rows[$rowIndex]
    }
    foreach ($stamp in $Stamps) {
        $xText, $yText, $key = $stamp.Split(',')
        $result[[int]$yText][[int]$xText] = [char]$key
    }
    return @($result | ForEach-Object { -join $_ })
}

$resolvedOutput = [System.IO.Path]::GetFullPath($OutputDirectory)
[System.IO.Directory]::CreateDirectory($resolvedOutput) | Out-Null

foreach ($part in @('tooth', 'skull', 'vertebra', 'limb', 'claw')) {
    $cleanRows = $cleanTextures[$part]
    $dirtyRows = Add-Dirt -Rows $cleanRows -Stamps $dirtStamps[$part]
    Write-PixelTexture -Path (Join-Path $resolvedOutput "$part.png") -Rows $cleanRows
    Write-PixelTexture -Path (Join-Path $resolvedOutput "dirty_$part.png") -Rows $dirtyRows
}

Write-Output "Generated clean and dirty fossil fragment textures in $resolvedOutput"
