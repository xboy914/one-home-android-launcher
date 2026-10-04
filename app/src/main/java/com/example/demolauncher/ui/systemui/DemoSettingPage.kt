package com.example.demolauncher.ui.systemui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.system.*

@Composable
fun DemoSettingPage(page: String, state: DemoSystemState, deviceStatus: DeviceStatus, phoneNumber: String, onToggle: (DemoToggle) -> Unit, onBack: () -> Unit) {
    val toggle = when (page) {
        "Wi-Fi" -> DemoToggle.WIFI
        "Bluetooth" -> DemoToggle.BLUETOOTH
        "Mobile network" -> DemoToggle.MOBILE_DATA
        else -> DemoToggle.BATTERY_SAVER
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Text(page, fontSize = 25.sp)
        }
        Spacer(Modifier.height(24.dp))
        Surface(shape = RoundedCornerShape(25.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(page, Modifier.weight(1f))
                if (page == "Wi-Fi" || page == "Mobile network") Text(
                    if (page == "Wi-Fi" && deviceStatus.wifiConnected || page == "Mobile network" && deviceStatus.cellularConnected) "Connected" else "Disconnected",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ) else Switch(enabled(state, toggle), onCheckedChange = { onToggle(toggle) })
            }
        }
        Spacer(Modifier.height(22.dp))
        val details = when (page) {
            "Wi-Fi" -> listOf("Connection" to if (deviceStatus.wifiConnected) "Connected to Wi-Fi" else "Not connected")
            "Bluetooth" -> listOf("Paired devices" to "Demo Buds", "" to "Demo Watch", "" to "Demo Car")
            "Mobile network" -> listOf("Connection" to if (deviceStatus.cellularConnected) "Mobile data connected" else "Mobile data not connected", "Signal" to (deviceStatus.cellularSignalLevel?.let { "$it/4" } ?: "Unavailable"), "Phone number" to phoneNumber)
            else -> listOf("Battery level" to (deviceStatus.batteryPercentage?.let { "$it%" } ?: "Unavailable"), "Power saving" to if (state.batterySaverEnabled) "On" else "Off")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            details.forEach { (label, value) -> item {
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Column(Modifier.fillMaxWidth().padding(18.dp)) {
                        if (label.isNotBlank()) Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(value, fontSize = 17.sp)
                    }
                }
            } }
        }
    }
}
