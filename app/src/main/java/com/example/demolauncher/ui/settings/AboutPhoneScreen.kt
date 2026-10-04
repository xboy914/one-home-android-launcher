package com.example.demolauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.model.MockDeviceProfile
import com.example.demolauncher.ui.localization.tr

private val page = Color(0xFFF1F1F3)
private val grey = Color(0xFF89898E)

@Composable
fun AboutPhoneScreen(profile: MockDeviceProfile, onBack: () -> Unit = {}) {
    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding(),
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth().height(84.dp).padding(horizontal = 26.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Search, contentDescription = tr("Search settings"), tint = Color.Black)
                Spacer(Modifier.weight(1f))
                Text(tr("About phone"), fontSize = 23.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.width(20.dp))
                Icon(Icons.Rounded.ArrowForwardIos, contentDescription = tr("Back"), modifier = Modifier.clickable(onClick = onBack), tint = Color.Black)
            }
            Spacer(Modifier.height(32.dp))
            Text(profile.deviceName, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 28.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(18.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(shape = RoundedCornerShape(25.dp), color = Color(0xFFE7E7E9)) {
                    Text(tr("Edit"), Modifier.padding(horizontal = 28.dp, vertical = 9.dp), fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(Modifier.height(20.dp))
            AboutField(tr("Device name"), profile.deviceName)
            AboutField(tr("Model number"), profile.modelNumber)
            AboutField(tr("Serial number"), profile.serialNumber)
            SimHeader("SIM 1", Color(0xFF3983F5))
            AboutField(tr("Phone number"), profile.phoneNumber)
            AboutField(tr("Network"), "Irancell")
            AboutField("IMEI ۱", profile.imei1)
            SimHeader("SIM 2", Color(0xFF7950D9))
            AboutField(tr("Phone number"), "+10000000001")
            AboutField(tr("Network"), "IR-MCI")
            AboutField("IMEI ۲", profile.imei2)
            Spacer(Modifier.height(28.dp))
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))) {
                listOf("Status information", "Legal information", "Software information", "Battery information").forEachIndexed { index, title ->
                    Box(Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 20.dp), contentAlignment = Alignment.CenterEnd) {
                        Text(tr(title), color = MaterialTheme.colorScheme.onSurface, fontSize = 19.sp)
                    }
                    if (index < 3) HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = Color(0xFFE1E1E4))
                }
            }
            Spacer(Modifier.height(22.dp))
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp).background(Color(0xFFE2E8F7), RoundedCornerShape(32.dp)).padding(24.dp), horizontalAlignment = Alignment.End) {
                Text(tr("Looking for something else?"), color = MaterialTheme.colorScheme.onSurface, fontSize = 19.sp)
                Spacer(Modifier.height(15.dp))
                listOf("Software update", "Reset", "Contact us").forEach {
                    Text(tr(it), Modifier.padding(vertical = 11.dp), color = Color(0xFF3975C8), fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable private fun AboutField(label: String, value: String) {
    Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 34.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(value, Modifier.weight(1f), fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
    }
}

@Composable private fun SimHeader(title: String, color: Color) {
    Row(Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 34.dp), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFB3B3B7))
        Spacer(Modifier.width(16.dp))
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(5.dp))
        Box(Modifier.size(16.dp).background(color, RoundedCornerShape(3.dp)))
    }
}
