package com.example.demolauncher.ui.systemui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.system.*
import com.example.demolauncher.ui.launcher.rememberSystemClock

private data class TileSpec(val toggle: DemoToggle, val title: String, val icon: ImageVector, val page: String? = null)
private val tiles = listOf(
    TileSpec(DemoToggle.WIFI, "Wi-Fi", Icons.Default.Wifi, "Wi-Fi"),
    TileSpec(DemoToggle.MOBILE_DATA, "Mobile data", Icons.Default.SignalCellularAlt, "Mobile network"),
    TileSpec(DemoToggle.BLUETOOTH, "Bluetooth", Icons.Default.Bluetooth, "Bluetooth"),
    TileSpec(DemoToggle.FLASHLIGHT, "Flashlight", Icons.Default.FlashlightOn),
    TileSpec(DemoToggle.AIRPLANE, "Airplane mode", Icons.Default.Flight),
    TileSpec(DemoToggle.DARK_MODE, "Dark mode", Icons.Default.DarkMode),
    TileSpec(DemoToggle.AUTO_ROTATE, "Auto rotate", Icons.Default.ScreenRotation),
    TileSpec(DemoToggle.DND, "Do not disturb", Icons.Default.DoNotDisturb),
    TileSpec(DemoToggle.LOCATION, "Location", Icons.Default.LocationOn),
    TileSpec(DemoToggle.BATTERY_SAVER, "Battery saver", Icons.Default.BatterySaver, "Battery"),
)

@Composable
fun QuickSettingsShade(
    state: DemoSystemState, deviceStatus: DeviceStatus, expanded: Boolean, onToggle: (DemoToggle) -> Unit,
    onBrightness: (Float) -> Unit, onVolume: (Float) -> Unit, onClear: () -> Unit,
    onExpand: () -> Unit, onSettings: () -> Unit, onPower: () -> Unit, onPage: (String) -> Unit,
    onDrag: (Float) -> Unit, onDragEnd: (Float) -> Unit,
) {
    val clock by rememberSystemClock()
    val surface = MaterialTheme.colorScheme.surface
    val foreground = MaterialTheme.colorScheme.onSurface
    var dragTotal by remember { mutableFloatStateOf(0f) }
    Column(Modifier.fillMaxSize().background(surface, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).navigationBarsPadding().testTag("quickSettingsShade")) {
        Row(Modifier.fillMaxWidth().pointerInput(expanded) {
            detectVerticalDragGestures(
                onVerticalDrag = { _, amount -> dragTotal += amount; onDrag(amount) },
                onDragEnd = { onDragEnd(dragTotal * 10f); dragTotal = 0f },
            )
        }.padding(start = 22.dp, end = 18.dp, top = 20.dp, bottom = 8.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(clock.time, fontSize = if (expanded) 42.sp else 30.sp, fontWeight = FontWeight.Light, color = foreground)
                Text(clock.date, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onSettings, modifier = Modifier.testTag("shadeSettings")) { Icon(Icons.Default.Settings, "Demo settings") }
            IconButton(onClick = onPower) { Icon(Icons.Default.PowerSettingsNew, "Demo power") }
        }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    (if (expanded) tiles else tiles.take(6)).chunked(2).forEach { pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            pair.forEach { tile ->
                                QuickSettingTile(
                                    tile,
                                    if (tile.toggle == DemoToggle.WIFI) deviceStatus.wifiConnected else if (tile.toggle == DemoToggle.MOBILE_DATA) deviceStatus.cellularConnected else enabled(state, tile.toggle),
                                    { if (tile.toggle == DemoToggle.WIFI || tile.toggle == DemoToggle.MOBILE_DATA) tile.page?.let(onPage) else onToggle(tile.toggle) },
                                    { tile.page?.let(onPage) },
                                    Modifier.weight(1f),
                                )
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(26.dp)).padding(horizontal = 18.dp, vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brightness6, null)
                        Slider(state.brightness, onBrightness, Modifier.weight(1f).padding(start = 12.dp).testTag("demoBrightness"))
                    }
                    if (expanded) Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, null)
                        Slider(state.volume, onVolume, Modifier.weight(1f).padding(start = 12.dp).testTag("demoVolume"))
                    }
                }
            }
            if (!expanded) item {
                TextButton(onClick = onExpand, Modifier.fillMaxWidth().testTag("expandShade")) { Text("Show all quick settings") }
            }
            if (expanded) {
                item {
                    Text("Device control", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Surface(Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Devices, null)
                            Spacer(Modifier.width(12.dp))
                            Text("Demo devices", Modifier.weight(1f))
                            Text("No devices", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifications", Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        TextButton(onClick = onClear, modifier = Modifier.testTag("clearDemoNotifications")) { Text("Clear") }
                    }
                }
                items(state.notifications, key = { it.id }) { notification ->
                    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Column(Modifier.padding(18.dp)) {
                            Text(notification.appName, fontWeight = FontWeight.SemiBold)
                            Text(notification.title)
                            Text(notification.message, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickSettingTile(tile: TileSpec, active: Boolean, onClick: () -> Unit, onLongClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    Row(modifier.height(58.dp).background(bg, RoundedCornerShape(22.dp))
        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        .testTag("tile_${tile.toggle.name}"), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.padding(start = 10.dp).size(36.dp).background(if (active) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = .35f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(tile.icon, null, Modifier.size(20.dp), tint = if (active) MaterialTheme.colorScheme.onPrimary else fg)
        }
        Text(if (tile.toggle == DemoToggle.WIFI || tile.toggle == DemoToggle.MOBILE_DATA) "${tile.title} · device" else tile.title, Modifier.padding(horizontal = 9.dp), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = fg, maxLines = 1)
    }
}
