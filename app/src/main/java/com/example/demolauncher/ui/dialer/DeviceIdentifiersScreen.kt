package com.example.demolauncher.ui.dialer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.model.MockDeviceProfile

private val panel = Color(0xFF252527)
private val muted = Color(0xFFC1C1C5)

@Composable
fun DeviceIdentifiersScreen(profile: MockDeviceProfile, onOk: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black).safeDrawingPadding(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp)
                .background(panel, RoundedCornerShape(28.dp))
                .padding(start = 24.dp, end = 24.dp, top = 26.dp, bottom = 16.dp),
        ) {
            Text("IMEI(MEID) and S/N", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
            Identifier("IMEI1", profile.imei1, "01")
            Identifier("IMEI2", profile.imei2, "01")
            Identifier("EID", "00000000000000000000000000000000")
            Identifier("SN", profile.serialNumber)
            TextButton(onClick = onOk, modifier = Modifier.align(Alignment.CenterHorizontally).height(56.dp)) {
                Text("OK", color = Color.White, fontSize = 21.sp)
            }
        }
    }
}

@Composable
private fun Identifier(label: String, value: String, suffix: String? = null) {
    Spacer(Modifier.height(9.dp))
    Text(label, color = muted, fontSize = 16.sp)
    Text(if (suffix == null) value else "$value / $suffix", color = muted, fontSize = 16.sp, maxLines = 1)
    Spacer(Modifier.height(6.dp))
    // A deterministic Code 128 style visual for the simulated identifier.
    val bars = ("104" + value + "106").flatMap { ch ->
        val seed = ch.code
        listOf(1 + seed % 3, 1 + seed / 3 % 2, 1 + seed / 7 % 4, 1 + seed / 11 % 2)
    }
    Canvas(Modifier.fillMaxWidth().height(72.dp).background(Color.White)) {
        val left = size.width * .09f
        val usable = size.width * .82f
        val unit = usable / bars.sum()
        var x = left
        bars.forEachIndexed { index, width ->
            val actual = width * unit
            if (index % 2 == 0) drawLine(Color.Black, Offset(x + actual / 2, 0f), Offset(x + actual / 2, size.height), strokeWidth = actual)
            x += actual
        }
    }
}
