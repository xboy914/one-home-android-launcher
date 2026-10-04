package com.example.demolauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.ui.localization.tr
import com.example.demolauncher.ui.localization.translate
import com.example.demolauncher.ui.localization.LocalAppLanguage
import kotlinx.coroutines.launch

private val canvas = Color(0xFFF1F1F3)
private data class Entry(val title: String, val subtitle: String, val icon: ImageVector, val color: Color)
private val sections = listOf(
    listOf(Entry("Connections", "Wi-Fi · Bluetooth · SIM manager", Icons.Default.Wifi, Color(0xFF3983F5)), Entry("Connected devices", "Quick Share · Android Auto", Icons.Default.Devices, Color(0xFF3983F5))),
    listOf(Entry("Modes and Routines", "Modes · Routines", Icons.Default.AutoAwesome, Color(0xFF7753F5)), Entry("Sounds and vibration", "Sound mode · Ringtone", Icons.Default.VolumeUp, Color(0xFF7753F5)), Entry("Notifications", "Status bar · Do not disturb", Icons.Default.Notifications, Color(0xFFE9681C))),
    listOf(Entry("Display", "Brightness · Eye comfort shield · Navigation bar", Icons.Default.Brightness6, Color(0xFFA2D626)), Entry("Battery", "Power saving · Charging", Icons.Default.BatteryFull, Color(0xFF55C644))),
    listOf(Entry("Wallpaper and style", "Wallpapers · Color palette", Icons.Default.Wallpaper, Color(0xFFE55081)), Entry("Themes", "Themes · Wallpapers · Icons", Icons.Default.Palette, Color(0xFFE55081)), Entry("Home screen", "Layout · App icon badges", Icons.Default.Home, Color(0xFF3983F5)), Entry("Lock screen and AOD", "Lock screen · Always On Display", Icons.Default.Lock, Color(0xFF3983F5))),
    listOf(Entry("Security and privacy", "Auto Blocker · Permission usage", Icons.Default.Shield, Color(0xFF7753F5)), Entry("Location", "Location requests", Icons.Default.LocationOn, Color(0xFF7753F5)), Entry("Safety and emergency", "Medical info · Emergency alerts", Icons.Default.Warning, Color(0xFFD83A39))),
    listOf(Entry("Accounts and backup", "Manage accounts · Smart Switch", Icons.Default.Sync, Color(0xFF3983F5)), Entry("Google", "Google services", Icons.Default.Public, Color(0xFF3983F5))),
    listOf(Entry("Advanced features", "Labs · Side button", Icons.Default.Settings, Color(0xFFFFC351))),
    listOf(Entry("Digital Wellbeing and parental controls", "Screen time · App timers", Icons.Default.Favorite, Color(0xFF55C644)), Entry("Device care", "Storage · Memory · App protection", Icons.Default.HealthAndSafety, Color(0xFF6769A8)), Entry("Apps", "Default apps · App settings", Icons.Default.Apps, Color(0xFF3983F5))),
    listOf(Entry("General management", "Language and keyboard · Date and time", Icons.Default.Tune, Color(0xFF6769A8)), Entry("Accessibility", "Visibility · Hearing · Interaction", Icons.Default.Accessibility, Color(0xFF55C644))),
    listOf(Entry("Software update", "Download and install", Icons.Default.SystemUpdate, Color(0xFF3983F5)), Entry("User manual", "Learn more", Icons.Default.Help, Color(0xFFFFC351)), Entry("Remote support", "Remote support", Icons.Default.HeadsetMic, Color(0xFFFFC351)), Entry("About phone", "Status · Legal information · Phone name", Icons.Default.Info, Color(0xFF6769A8))),
)

@Composable
fun SettingsScreen(deviceName: String, onAbout: () -> Unit, onHomeRole: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    val language = LocalAppLanguage.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val placeholder = tr("Download to get started")
    val visible = sections.map { section -> section.filter { search.isBlank() || it.title.contains(search, true) || translate(it.title, language).contains(search, true) } }.filter { it.isNotEmpty() }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).safeDrawingPadding().testTag("settingsList"), contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Row(Modifier.fillMaxWidth().height(88.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { searching = !searching }) { Icon(Icons.Default.Search, contentDescription = tr("Search settings"), tint = Color.Black) }
                    Spacer(Modifier.weight(1f))
                    Text(tr("Settings"), fontSize = 23.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
                if (searching) OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth().padding(horizontal = 12.dp), placeholder = { Text(tr("Search settings")) }, singleLine = true, shape = RoundedCornerShape(26.dp))
                else Spacer(Modifier.height(2.dp))
            }
            if (search.isBlank()) item {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp)).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(65.dp).background(Color(0xFF8DACEB), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, Modifier.size(36.dp), tint = Color.White) }
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(tr("Sign in to your Galaxy"), fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Text(tr("Find your lost phone"), color = Color(0xFF698AC7), fontSize = 14.sp)
                    }
                }
            }
            visible.forEach { section ->
                item {
                    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp)).padding(horizontal = 20.dp)) {
                        section.forEachIndexed { index, entry ->
                            Row(Modifier.fillMaxWidth().heightIn(min = 78.dp).clickable { if (entry.title == "About phone") onAbout() else scope.launch { snackbar.showSnackbar(placeholder) } }.padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text(tr(entry.title), color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, maxLines = 1)
                                    Text(tr(entry.subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, maxLines = 1)
                                }
                                Spacer(Modifier.width(18.dp))
                                Box(Modifier.size(32.dp).background(entry.color, CircleShape), contentAlignment = Alignment.Center) { Icon(entry.icon, null, Modifier.size(20.dp), tint = Color.White) }
                            }
                            if (index < section.lastIndex) HorizontalDivider(color = Color(0xFFE3E3E5))
                        }
                    }
                }
            }
            if (search.isBlank()) item { TextButton(onClick = onHomeRole, modifier = Modifier.fillMaxWidth()) { Text(tr("Set as default Home app")) } }
        }
    }
}
