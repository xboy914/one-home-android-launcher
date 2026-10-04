package com.example.demolauncher.ui.dialer

import com.example.demolauncher.ui.localization.tr

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.domain.SecretCode
import kotlinx.coroutines.launch

private val CallGreen = Color(0xFF35AA66)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialerScreen(viewModel: DialerViewModel, onCode: (SecretCode) -> Unit, onCall: (String) -> Unit) {
    val input by viewModel.input.collectAsState()
    val demoMessage = tr("Download to get started")
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(containerColor = MaterialTheme.colorScheme.background, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).safeDrawingPadding().padding(horizontal = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 22.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(tr("Phone"), fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.Bold)
                Text("⋮", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 26.sp)
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().height(68.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(36.dp))
                Text(
                    input.ifEmpty { tr("Enter a number") },
                    Modifier.weight(1f),
                    fontSize = if (input.isEmpty()) 19.sp else 31.sp,
                    lineHeight = 38.sp,
                    color = if (input.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Box(Modifier.size(36.dp).combinedClickable(onClick = viewModel::backspace, onLongClick = viewModel::clear), contentAlignment = Alignment.Center) {
                    if (input.isNotEmpty()) Icon(Icons.AutoMirrored.Filled.Backspace, tr("Delete"), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            DialPad(onKey = { key -> viewModel.append(key)?.let(onCode) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(18.dp))
            FilledIconButton(
                onClick = { if (input.isNotBlank()) onCall(input) },
                modifier = Modifier.size(70.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CallGreen),
            ) { Icon(Icons.Default.Call, tr("Call"), Modifier.size(30.dp), tint = Color.White) }
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .45f))
            Row(Modifier.fillMaxWidth().height(72.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                BottomTab("Keypad", Icons.Default.Dialpad, true) { }
                BottomTab("Recents", Icons.Default.History, false) { scope.launch { snackbar.showSnackbar(demoMessage) } }
                BottomTab("Contacts", Icons.Default.People, false) { scope.launch { snackbar.showSnackbar(demoMessage) } }
            }
        }
    }
}

@Composable
private fun BottomTab(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(Modifier.width(94.dp).height(64.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(25.dp))
        Spacer(Modifier.height(3.dp))
        Text(tr(label), color = color, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}
