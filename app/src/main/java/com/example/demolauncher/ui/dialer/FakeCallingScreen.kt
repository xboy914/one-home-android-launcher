package com.example.demolauncher.ui.dialer

import com.example.demolauncher.ui.localization.tr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.model.FakeCallState
import kotlinx.coroutines.delay

private val CallWhite = Color(0xFFF5F7FC)
private val CallMuted = Color(0xFFC5CCDB)

@Composable
fun FakeCallingScreen(number: String, onEnd: () -> Unit) {
    var state by remember(number) { mutableStateOf(FakeCallState(number)) }
    var tones by remember(number) { mutableStateOf("") }
    val currentOnEnd by rememberUpdatedState(onEnd)
    LaunchedEffect(number) {
        delay(2_000)
        state = state.copy(connected = true)
        delay(1_000)
        state = state.copy(connectedSeconds = 1)
        delay(500)
        currentOnEnd()
    }
    val background = Brush.verticalGradient(listOf(Color(0xFF34445C), Color(0xFF1C273B), Color(0xFF101926)))
    Column(Modifier.fillMaxSize().background(background).safeDrawingPadding().padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(34.dp))
        Text(tr("CALL"), color = CallMuted, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(.45f))
        Box(Modifier.size(104.dp).background(Color.White.copy(alpha = .13f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(58.dp), tint = CallWhite)
        }
        Spacer(Modifier.height(25.dp))
        Text(number, color = CallWhite, fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        Spacer(Modifier.height(9.dp))
        Text(
            if (state.connected) "%02d:%02d".format(state.connectedSeconds / 60, state.connectedSeconds % 60) else tr("Calling..."),
            color = CallMuted, style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(12.dp))
        Text(tr("No phone connection"), color = CallMuted.copy(alpha = .72f), style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.weight(1f))
        Surface(shape = RoundedCornerShape(36.dp), color = Color.White.copy(alpha = .12f), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 20.dp)) {
                if (state.keypad) {
                    Text(tones.ifEmpty { tr("Keypad") }, Modifier.fillMaxWidth(), color = CallWhite, fontSize = 20.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(10.dp))
                    DialPad(onKey = { tones += it }, modifier = Modifier.fillMaxWidth(), compact = true)
                    TextButton(onClick = { state = state.copy(keypad = false) }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(tr("Hide keypad"), color = CallWhite) }
                } else {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        CallControl("Add call", Icons.Default.Add, state.addCall) { state = state.copy(addCall = !state.addCall) }
                        CallControl("Video", Icons.Default.Videocam, state.video) { state = state.copy(video = !state.video) }
                        CallControl("Bluetooth", Icons.Default.Bluetooth, state.bluetooth) { state = state.copy(bluetooth = !state.bluetooth) }
                    }
                    Spacer(Modifier.height(22.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        CallControl("Speaker", Icons.AutoMirrored.Filled.VolumeUp, state.speaker) { state = state.copy(speaker = !state.speaker) }
                        CallControl("Mute", Icons.Default.MicOff, state.muted) { state = state.copy(muted = !state.muted) }
                        CallControl("Keypad", Icons.Default.Dialpad, state.keypad) { state = state.copy(keypad = true) }
                    }
                }
            }
        }
        Spacer(Modifier.height(30.dp))
        FilledIconButton(
            onClick = onEnd,
            modifier = Modifier.size(76.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE74E59)),
        ) { Icon(Icons.Default.CallEnd, contentDescription = tr("End call"), modifier = Modifier.size(32.dp), tint = Color.White) }
        Spacer(Modifier.height(34.dp))
    }
}

@Composable
private fun CallControl(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.width(92.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(62.dp).background(if (selected) CallWhite else Color.White.copy(alpha = .16f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = tr(label), tint = if (selected) Color(0xFF1C273B) else CallWhite, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(tr(label), color = CallWhite, fontSize = 12.sp, maxLines = 1)
    }
}
