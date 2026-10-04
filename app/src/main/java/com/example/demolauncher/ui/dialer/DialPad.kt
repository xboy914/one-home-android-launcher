package com.example.demolauncher.ui.dialer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val keys = listOf(
    "1" to "", "2" to "ABC", "3" to "DEF",
    "4" to "GHI", "5" to "JKL", "6" to "MNO",
    "7" to "PQRS", "8" to "TUV", "9" to "WXYZ",
    "*" to "", "0" to "+", "#" to "",
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialPad(onKey: (String) -> Unit, modifier: Modifier = Modifier, compact: Boolean = false) {
    val keySize = if (compact) 58.dp else 72.dp
    val rowHeight = if (compact) 66.dp else 78.dp
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 4.dp)) {
        keys.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth().height(rowHeight), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                row.forEach { (number, letters) ->
                    Surface(
                        shape = CircleShape,
                        color = if (compact) Color.White.copy(alpha = .13f) else MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.size(keySize).combinedClickable(
                            onClick = { onKey(number) },
                            onLongClick = { onKey(if (number == "0") "+" else number) },
                        ),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(number, fontSize = if (compact) 26.sp else 30.sp, lineHeight = 32.sp, fontWeight = FontWeight.Normal, color = if (compact) Color.White else MaterialTheme.colorScheme.onSurface)
                            if (letters.isNotEmpty()) Text(letters, fontSize = 9.sp, lineHeight = 11.sp, letterSpacing = 1.4.sp, color = if (compact) Color.White.copy(alpha = .72f) else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
    }
}
