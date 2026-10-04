package com.example.demolauncher.ui.systemui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.system.*
import com.example.demolauncher.data.model.AppLanguage
import com.example.demolauncher.ui.localization.LocalAppLanguage
import com.example.demolauncher.ui.launcher.rememberSystemClock
import kotlinx.coroutines.launch

internal enum class ShadeExpansion { CLOSED, COMPACT, EXPANDED }
internal enum class DemoPower { ON, OFF, RESTARTING }

@Composable
fun DemoSystemShell(
    state: DemoSystemState,
    deviceStatus: DeviceStatus,
    onToggle: (DemoToggle) -> Unit,
    onBrightness: (Float) -> Unit,
    onVolume: (Float) -> Unit,
    onClear: () -> Unit,
    onSettings: () -> Unit,
    onRestart: () -> Unit,
    phoneNumber: String,
    specialDarkStatus: Boolean = false,
    isLauncher: Boolean = false,
    content: @Composable () -> Unit,
) {
    var expansion by remember { mutableStateOf(ShadeExpansion.CLOSED) }
    var drag by remember { mutableFloatStateOf(0f) }
    var page by remember { mutableStateOf<String?>(null) }
    var powerDialog by remember { mutableStateOf(false) }
    var power by remember { mutableStateOf(DemoPower.ON) }
    val scope = rememberCoroutineScope()
    val progress by animateFloatAsState(if (expansion == ShadeExpansion.CLOSED) 0f else 1f, label = "shade")
    val activeProgress = (progress + drag).coerceIn(0f, 1f)
    val background = if (isLauncher) {
        if (state.darkModeEnabled) Color(0xFF44316F) else Color(0xFFB784E8)
    } else MaterialTheme.colorScheme.background
    val foreground = if (specialDarkStatus || isLauncher) Color.White else MaterialTheme.colorScheme.onBackground

    BackHandler(expansion != ShadeExpansion.CLOSED || page != null || power != DemoPower.ON) {
        when {
            power != DemoPower.ON -> power = DemoPower.ON
            page != null -> page = null
            expansion == ShadeExpansion.EXPANDED -> expansion = ShadeExpansion.COMPACT
            else -> expansion = ShadeExpansion.CLOSED
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize().background(background)) {
        val fullHeight = maxHeight
        val compactHeight = maxHeight * .52f
        Box(Modifier.fillMaxSize().padding(top = 32.dp).navigationBarsPadding()) { content() }
        if (state.brightness < .35f) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = (.35f - state.brightness) * .35f)))
        if (activeProgress > .001f) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = activeProgress * .36f)).clickable { expansion = ShadeExpansion.CLOSED })
        }
        if (activeProgress > .001f || expansion != ShadeExpansion.CLOSED) {
            Box(Modifier.fillMaxWidth().height(if (expansion == ShadeExpansion.EXPANDED) fullHeight else compactHeight)
                .offset(y = -(if (expansion == ShadeExpansion.EXPANDED) fullHeight else compactHeight) * (1f - activeProgress))) {
                if (page == null) QuickSettingsShade(
                    state = state, deviceStatus = deviceStatus, expanded = expansion == ShadeExpansion.EXPANDED,
                    onToggle = onToggle, onBrightness = onBrightness, onVolume = onVolume,
                    onClear = onClear, onExpand = { expansion = ShadeExpansion.EXPANDED },
                    onSettings = { expansion = ShadeExpansion.CLOSED; onSettings() },
                    onPower = { powerDialog = true }, onPage = { page = it },
                    onDrag = { delta -> drag = (drag + delta / 900f).coerceIn(-1f, 1f) },
                    onDragEnd = { velocity ->
                        expansion = when {
                            velocity < -500f || drag < -.35f -> ShadeExpansion.CLOSED
                            velocity > 500f || drag > .35f -> ShadeExpansion.EXPANDED
                            else -> expansion
                        }
                        drag = 0f
                    },
                ) else DemoSettingPage(page!!, state, deviceStatus, phoneNumber, onToggle) { page = null }
            }
        }
        if (expansion == ShadeExpansion.CLOSED && page == null && power == DemoPower.ON) {
            CustomStatusBar(state, deviceStatus, foreground, { expansion = ShadeExpansion.COMPACT }) { delta, velocity ->
                if (delta > 30f || velocity > 500f) expansion = ShadeExpansion.COMPACT
            }
        }
        if (power != DemoPower.ON) {
            Box(Modifier.fillMaxSize().background(Color.Black).clickable { power = DemoPower.ON }, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (power == DemoPower.OFF) "Demo device powered off" else "Demo Device", color = Color.White, fontSize = 24.sp)
                    if (power == DemoPower.RESTARTING) CircularProgressIndicator(Modifier.padding(24.dp), color = Color.White)
                    else Text("Tap to turn on", color = Color.LightGray)
                }
            }
        }
    }
    if (powerDialog) AlertDialog(
        onDismissRequest = { powerDialog = false },
        title = { Text("Demo power") },
        text = { Column {
            TextButton(onClick = { powerDialog = false; expansion = ShadeExpansion.CLOSED; power = DemoPower.OFF }) { Text("Power off") }
            TextButton(onClick = { powerDialog = false; expansion = ShadeExpansion.CLOSED; power = DemoPower.RESTARTING; onRestart(); scope.launch { kotlinx.coroutines.delay(1700); power = DemoPower.ON } }) { Text("Restart") }
            TextButton(onClick = { powerDialog = false }) { Text("Emergency mode") }
        } },
        confirmButton = { TextButton(onClick = { powerDialog = false }) { Text("Cancel") } },
    )
}

@Composable
private fun CustomStatusBar(state: DemoSystemState, deviceStatus: DeviceStatus, color: Color, onClick: () -> Unit, onDragEnd: (Float, Float) -> Unit) {
    val clock by rememberSystemClock()
    val persian = LocalAppLanguage.current == AppLanguage.PERSIAN
    var total by remember { mutableFloatStateOf(0f) }
    Row(Modifier.fillMaxWidth().height(32.dp).testTag("customStatusBar")
        .pointerInput(Unit) { detectVerticalDragGestures(onVerticalDrag = { _, amount -> total += amount }, onDragEnd = { onDragEnd(total, 0f); total = 0f }) }
        .clickable(onClick = onClick).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(clock.time, color = color, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        if (state.notifications.isNotEmpty()) {
            Spacer(Modifier.width(7.dp))
            Icon(Icons.Default.Notifications, "Demo notifications", Modifier.size(13.dp), tint = color.copy(alpha = .85f))
        }
        Spacer(Modifier.weight(1f))
        if (state.doNotDisturbEnabled) {
            Icon(Icons.Default.DoNotDisturb, "Do not disturb", Modifier.size(14.dp), tint = color)
            Spacer(Modifier.width(4.dp))
        }
        if (deviceStatus.wifiConnected) {
            Icon(Icons.Default.Wifi, "Wi-Fi connected", Modifier.size(15.dp).testTag("statusWifi"), tint = color)
            Spacer(Modifier.width(4.dp))
        }
        deviceStatus.cellularSignalLevel?.let { level ->
            SignalBars(level, color)
            Spacer(Modifier.width(5.dp))
        }
        Row(Modifier.background(color.copy(alpha = .19f), RoundedCornerShape(12.dp)).padding(horizontal = 5.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (deviceStatus.charging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull, "Battery", Modifier.size(13.dp), tint = color)
            Spacer(Modifier.width(2.dp))
            val percentage = deviceStatus.batteryPercentage?.toString() ?: "—"
            val shown = if (persian) percentage.map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("") else percentage
            Text(if (deviceStatus.batteryPercentage == null) shown else "$shown%", Modifier.testTag("statusBattery"), color = color, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SignalBars(level: Int, color: Color) {
    val visibleLevel = level.coerceIn(0, 4)
    Canvas(Modifier.size(width = 17.dp, height = 14.dp)) {
        val gap = size.width / 5f
        repeat(4) { index ->
            val barHeight = size.height * (.35f + index * .20f)
            drawRect(color.copy(alpha = if (index < visibleLevel) 1f else .35f),
                topLeft = Offset(index * gap, size.height - barHeight),
                size = Size(gap * .67f, barHeight))
        }
    }
}
