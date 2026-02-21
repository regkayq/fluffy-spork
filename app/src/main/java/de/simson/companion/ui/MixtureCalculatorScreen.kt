package de.simson.companion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.simson.companion.ui.theme.*
import kotlin.math.max
import kotlin.math.min

// ─── Data model ─────────────────────────────────────────────────────────────

data class MixtureRatio(
    val ratio: Int,          // 1:ratio
    val label: String,       // short label
    val description: String  // model hint
)

val MIXTURE_RATIOS = listOf(
    MixtureRatio(25, "1:25", "EINFAHREN"),
    MixtureRatio(33, "1:33", "SCHWALBE"),
    MixtureRatio(40, "1:40", "SR4 / KR51"),
    MixtureRatio(50, "1:50", "S51 / S70"),
)

// ─── Main screen ─────────────────────────────────────────────────────────────

@Composable
fun MixtureCalculatorScreen() {
    var fuelLiters by remember { mutableStateOf(2.0f) }
    var selectedRatioIndex by remember { mutableStateOf(3) } // default 1:50

    val selected = MIXTURE_RATIOS[selectedRatioIndex]
    val oilMl    = fuelLiters * 1000f / selected.ratio
    val totalMl  = fuelLiters * 1000f + oilMl

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DDRBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header banner ────────────────────────────────────────────────
            DDRBanner()

            // ── Title block ──────────────────────────────────────────────────
            DDRTitleBlock()

            Spacer(Modifier.height(12.dp))

            // ── Fuel input ───────────────────────────────────────────────────
            DDRPanel(
                title = "KRAFTSTOFF-MENGE",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                FuelInputRow(
                    fuelLiters = fuelLiters,
                    onDecrement = { fuelLiters = max(0.5f, fuelLiters - 0.5f) },
                    onIncrement = { fuelLiters = min(25f, fuelLiters + 0.5f) },
                    onValueChange = { raw ->
                        val v = raw.toFloatOrNull()
                        if (v != null && v > 0f && v <= 25f) fuelLiters = v
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Ratio selector ───────────────────────────────────────────────
            DDRPanel(
                title = "MISCHUNGSVERHÄLTNIS",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                RatioSelectorGrid(
                    ratios = MIXTURE_RATIOS,
                    selectedIndex = selectedRatioIndex,
                    onSelect = { selectedRatioIndex = it }
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Result display ───────────────────────────────────────────────
            DDRPanel(
                title = "ÖL-BEIMISCHUNG",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                PhosphorDisplay(oilMl = oilMl)
            }

            Spacer(Modifier.height(8.dp))

            // ── Info box ─────────────────────────────────────────────────────
            DDRInfoBox(
                ratio = selected,
                fuelLiters = fuelLiters,
                oilMl = oilMl,
                totalMl = totalMl,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // ── Footer ───────────────────────────────────────────────────────
            DDRFooter()

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Header components ────────────────────────────────────────────────────────

@Composable
private fun DDRBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DDRRed)
            .padding(vertical = 6.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "★  VOLKSEIGENER BETRIEB SIMSON  ★",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun DDRTitleBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DDRPanel)
            .drawBehind {
                // amber bottom accent line
                drawLine(
                    color = DDRAmber,
                    start = Offset(0f, size.height - 2f),
                    end   = Offset(size.width, size.height - 2f),
                    strokeWidth = 2f
                )
            }
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SIMSON",
            color = DDRAmber,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 44.sp,
            letterSpacing = 8.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "MOPED  BEGLEITER",
            color = DDRTextPrimary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 4.sp
        )
        Spacer(Modifier.height(6.dp))
        HorizontalDivider(
            modifier  = Modifier.padding(horizontal = 40.dp),
            color     = DDRBorderNormal,
            thickness = 1.dp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "VEB FAHRZEUG- UND JAGDWAFFENWERK · SUHL/THÜR.",
            color = DDRTextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            letterSpacing = 1.sp
        )
    }
}

// ─── Reusable panel wrapper ───────────────────────────────────────────────────

@Composable
fun DDRPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .border(width = 1.dp, color = DDRBorderNormal)
    ) {
        // Panel title bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DDRPanelHeader)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "▶",
                color = DDRRedBright,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = title,
                color = DDRAmber,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.weight(1f))
            // Corner screws decoration
            Text(
                text = "◈",
                color = DDRBorderAccent,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp
            )
        }
        HorizontalDivider(color = DDRBorderNormal, thickness = 1.dp)
        // Content area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DDRPanel)
                .padding(12.dp),
            content = content
        )
    }
}

// ─── Fuel input ───────────────────────────────────────────────────────────────

@Composable
private fun FuelInputRow(
    fuelLiters: Float,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Decrement button
        IndustrialButton(
            label = "−",
            onClick = onDecrement,
            modifier = Modifier.size(48.dp)
        )

        // Value display / text field
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(DDRDisplayBg)
                .border(1.dp, DDRGreenDim),
            contentAlignment = Alignment.Center
        ) {
            var editText by remember(fuelLiters) { mutableStateOf("%.1f".format(fuelLiters)) }
            TextField(
                value = editText,
                onValueChange = { raw ->
                    editText = raw
                    onValueChange(raw)
                },
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    color = DDRGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                ),
                suffix = {
                    Text(
                        "L",
                        color = DDRGreenMid,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor    = DDRDisplayBg,
                    unfocusedContainerColor  = DDRDisplayBg,
                    focusedIndicatorColor    = Color.Transparent,
                    unfocusedIndicatorColor  = Color.Transparent,
                    cursorColor              = DDRGreen,
                ),
            )
        }

        // Increment button
        IndustrialButton(
            label = "+",
            onClick = onIncrement,
            modifier = Modifier.size(48.dp)
        )
    }
    Spacer(Modifier.height(6.dp))
    Text(
        text = "Schritte: 0.5 Liter  |  Max: 25 Liter",
        color = DDRTextSecondary,
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp
    )
}

@Composable
private fun IndustrialButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(DDRPanelHeader)
            .border(1.dp, DDRAmberDim)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = DDRAmber,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp
        )
    }
}

// ─── Ratio selector ───────────────────────────────────────────────────────────

@Composable
private fun RatioSelectorGrid(
    ratios: List<MixtureRatio>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ratios.forEachIndexed { index, ratio ->
            RatioButton(
                ratio = ratio,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RatioButton(
    ratio: MixtureRatio,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor     = if (selected) DDRAmberDim else DDRPanelHeader
    val borderColor = if (selected) DDRAmber    else DDRBorderNormal
    val labelColor  = if (selected) DDRAmber    else DDRTextSecondary
    val descColor   = if (selected) DDRYellow   else DDRTextDark

    Column(
        modifier = modifier
            .background(bgColor)
            .border(width = if (selected) 2.dp else 1.dp, color = borderColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = ratio.label,
            color = labelColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = ratio.description,
            color = descColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 7.sp,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )
        // Indicator LED
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    color = if (selected) DDRGreen else DDRTextDark,
                    shape = RoundedCornerShape(3.dp)
                )
        )
    }
}

// ─── Phosphor display ─────────────────────────────────────────────────────────

@Composable
private fun PhosphorDisplay(oilMl: Float) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main display - phosphor green CRT look
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DDRDisplayBg,
                            Color(0xFF061008),
                            DDRDisplayBg,
                        )
                    )
                )
                .border(2.dp, DDRGreenDim)
                .padding(vertical = 20.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Scan line effect (subtle horizontal lines via text)
                Text(
                    text = "· · · · · · · · · · · · · · · · · · · ·",
                    color = DDRGreenDim.copy(alpha = 0.3f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 6.sp
                )
                Spacer(Modifier.height(8.dp))

                // Main number - big phosphor readout
                Text(
                    text = "%.0f".format(oilMl),
                    color = DDRGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 72.sp,
                    lineHeight = 72.sp
                )
                Text(
                    text = "MILLILITER  ÖL",
                    color = DDRGreenMid,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 3.sp
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "· · · · · · · · · · · · · · · · · · · ·",
                    color = DDRGreenDim.copy(alpha = 0.3f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 6.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Oil volume bar
        OilVolumeBar(oilMl = oilMl)
    }
}

@Composable
private fun OilVolumeBar(oilMl: Float) {
    // Visually represents how much oil goes into the mixture.
    // Max reference is 40ml/L * 2L = 80ml (1:25 ratio, 2L fuel)
    // We normalise against a 1:25 worst-case reference for current inputs.
    val maxReference = 80f
    val fraction = (oilMl / maxReference).coerceIn(0f, 1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ÖL-ANTEIL",
                color = DDRTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "%.1f%%".format(fraction * 100f),
                color = DDRGreenMid,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp
            )
        }
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(DDRDisplayBg)
                .border(1.dp, DDRBorderDim)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(DDRGreenDim, DDRGreen)
                        )
                    )
            )
        }
    }
}

// ─── Info box ─────────────────────────────────────────────────────────────────

@Composable
private fun DDRInfoBox(
    ratio: MixtureRatio,
    fuelLiters: Float,
    oilMl: Float,
    totalMl: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DDRPanel)
            .border(1.dp, DDRBorderDim)
            .padding(10.dp)
    ) {
        InfoRow("VERHÄLTNIS",     "1 : ${ratio.ratio}  (${ratio.description})")
        InfoRow("ÖL PRO LITER",   "%.0f ml/L".format(1000f / ratio.ratio))
        InfoRow("KRAFTSTOFF",     "%.1f Liter".format(fuelLiters))
        InfoRow("ÖL BEIMISCHEN",  "%.0f ml".format(oilMl))
        HorizontalDivider(
            modifier  = Modifier.padding(vertical = 6.dp),
            color     = DDRBorderDim,
            thickness = 1.dp
        )
        InfoRow("GESAMTGEMISCH",  "%.0f ml  (%.2f L)".format(totalMl, totalMl / 1000f))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = DDRTextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            color = DDRTextPrimary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

// ─── Footer ───────────────────────────────────────────────────────────────────

@Composable
private fun DDRFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = DDRBorderNormal, thickness = 1.dp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = "★  SIMSON BEGLEITER  v1.0  ★",
            color = DDRTextDark,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            letterSpacing = 1.sp
        )
        Text(
            text = "Suhl, Thüringen · Deutsche Demokratische Republik",
            color = DDRTextDark,
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            letterSpacing = 0.5.sp
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0D0E0B)
@Composable
fun MixtureCalculatorPreview() {
    de.simson.companion.ui.theme.SimsonTheme {
        MixtureCalculatorScreen()
    }
}
