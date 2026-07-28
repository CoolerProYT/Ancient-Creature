Add-Type -AssemblyName System.Drawing

$outputPath = Join-Path $PSScriptRoot "..\..\common\src\main\resources\assets\ancientcreature\textures\item\egg_fossil.png"
$palette = @{
    "." = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    "0" = [System.Drawing.ColorTranslator]::FromHtml("#4B4A40")
    "1" = [System.Drawing.ColorTranslator]::FromHtml("#686554")
    "2" = [System.Drawing.ColorTranslator]::FromHtml("#827A62")
    "3" = [System.Drawing.ColorTranslator]::FromHtml("#A09370")
    "4" = [System.Drawing.ColorTranslator]::FromHtml("#B9A97D")
    "5" = [System.Drawing.ColorTranslator]::FromHtml("#D8C99D")
    "r" = [System.Drawing.ColorTranslator]::FromHtml("#895D3F")
    "p" = [System.Drawing.ColorTranslator]::FromHtml("#766E5B")
    "g" = [System.Drawing.ColorTranslator]::FromHtml("#626355")
}

$rows = @(
    "................"
    "................"
    "................"
    "......1111......"
    ".....134431....."
    "....13544421...."
    "....155444p0...."
    "...1354444320..."
    "...14p44443r0..."
    "...1444444330..."
    "...1444443r30..."
    "...1344433g20..."
    "....0g233r20...."
    ".....000000....."
    "................"
    "................"
)

$bitmap = [System.Drawing.Bitmap]::new(16, 16)
try {
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $bitmap.SetPixel($x, $y, $palette[[string]$rows[$y][$x]])
        }
    }
    $bitmap.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
}
finally {
    $bitmap.Dispose()
}
