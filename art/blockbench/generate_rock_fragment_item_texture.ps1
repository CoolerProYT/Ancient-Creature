Add-Type -AssemblyName System.Drawing

$outputPath = Join-Path $PSScriptRoot "..\..\common\src\main\resources\assets\ancientcreature\textures\item\rock_fragment.png"
$palette = @{
    "." = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    "0" = [System.Drawing.ColorTranslator]::FromHtml("#343A37")
    "1" = [System.Drawing.ColorTranslator]::FromHtml("#46504B")
    "2" = [System.Drawing.ColorTranslator]::FromHtml("#5C665F")
    "3" = [System.Drawing.ColorTranslator]::FromHtml("#71786F")
    "4" = [System.Drawing.ColorTranslator]::FromHtml("#899083")
    "5" = [System.Drawing.ColorTranslator]::FromHtml("#A4A895")
    "m" = [System.Drawing.ColorTranslator]::FromHtml("#66764C")
}

$rows = @(
    "................"
    "................"
    "................"
    "................"
    "......00110....."
    "....001245410..."
    "...01234443210.."
    "..012345433210.."
    "...0112333210..."
    "....0012m210...."
    ".....000110....."
    "................"
    "................"
    "................"
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
