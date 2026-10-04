package com.example.demolauncher.ui.launcher

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.model.AppIcon
import com.example.demolauncher.data.model.MockApplication
import com.example.demolauncher.ui.localization.tr

/** Prefer the real installed Samsung application icon on Galaxy devices. */
private fun samsungPackage(icon: AppIcon): String? = when (icon) {
    AppIcon.PHONE -> "com.samsung.android.dialer"
    AppIcon.MESSAGES -> "com.samsung.android.messaging"
    AppIcon.CAMERA -> "com.sec.android.app.camera"
    AppIcon.GALLERY -> "com.sec.android.gallery3d"
    AppIcon.SETTINGS -> "com.android.settings"
    AppIcon.CLOCK -> "com.sec.android.app.clockpackage"
    AppIcon.CALENDAR -> "com.samsung.android.calendar"
    AppIcon.CALCULATOR -> "com.sec.android.app.popupcalculator"
    AppIcon.CONTACTS -> "com.samsung.android.contacts"
    AppIcon.FILES -> "com.sec.android.app.myfiles"
    AppIcon.NOTES -> "com.samsung.android.app.notes"
    AppIcon.INTERNET -> "com.sec.android.app.sbrowser"
    AppIcon.MUSIC -> "com.sec.android.app.music"
    AppIcon.WEATHER -> "com.sec.android.daemonapp"
    AppIcon.STORE -> "com.sec.android.app.samsungapps"
    AppIcon.RECORDER -> "com.sec.android.app.voicenote"
}

private fun installedIcon(context: Context, icon: AppIcon) = try {
    samsungPackage(icon)?.let { context.packageManager.getApplicationIcon(it).toBitmap(144, 144).asImageBitmap() }
} catch (_: Exception) { null }

/** Original fallback symbols for devices without Samsung applications. */
private data class IconStyle(val top: Color, val bottom: Color, val glyph: Color = Color.White)

private fun style(icon: AppIcon): IconStyle = when (icon) {
    AppIcon.PHONE -> IconStyle(Color(0xFF62DC91), Color(0xFF19BD66))
    AppIcon.MESSAGES -> IconStyle(Color(0xFF72B7FF), Color(0xFF3786ED))
    AppIcon.CAMERA -> IconStyle(Color(0xFFFAFAFC), Color(0xFFDCE0E8), Color(0xFF384258))
    AppIcon.GALLERY -> IconStyle(Color(0xFFFF8795), Color(0xFFED476E))
    AppIcon.SETTINGS -> IconStyle(Color(0xFFB9C1CE), Color(0xFF7D899C))
    AppIcon.CLOCK -> IconStyle(Color(0xFF644DD0), Color(0xFF3D3EA6))
    AppIcon.CALENDAR -> IconStyle(Color(0xFFFFFFFF), Color(0xFFF0F2F8), Color(0xFF4287E5))
    AppIcon.CALCULATOR -> IconStyle(Color(0xFF94B9A2), Color(0xFF5C9B77))
    AppIcon.CONTACTS -> IconStyle(Color(0xFFFFB776), Color(0xFFEE8749))
    AppIcon.FILES -> IconStyle(Color(0xFFFFD778), Color(0xFFF2AE3D))
    AppIcon.NOTES -> IconStyle(Color(0xFFFFCA7E), Color(0xFFFF9D50))
    AppIcon.INTERNET -> IconStyle(Color(0xFF6557DD), Color(0xFF4056C2))
    AppIcon.MUSIC -> IconStyle(Color(0xFFFF8292), Color(0xFFFF4D65))
    AppIcon.WEATHER -> IconStyle(Color(0xFF91D8FF), Color(0xFF4AA9EF))
    AppIcon.STORE -> IconStyle(Color(0xFFBCA2FF), Color(0xFF8A6DE7))
    AppIcon.RECORDER -> IconStyle(Color(0xFFFF9B83), Color(0xFFF26A61))
}

private fun image(icon: AppIcon): ImageVector = when (icon) {
    AppIcon.PHONE -> Icons.Default.Call
    AppIcon.MESSAGES -> Icons.Default.ChatBubble
    AppIcon.CAMERA -> Icons.Default.CameraAlt
    AppIcon.GALLERY -> Icons.Default.FilterVintage
    AppIcon.SETTINGS -> Icons.Default.Settings
    AppIcon.CLOCK -> Icons.Default.AccessTime
    AppIcon.CALENDAR -> Icons.Default.DateRange
    AppIcon.CALCULATOR -> Icons.Default.Calculate
    AppIcon.CONTACTS -> Icons.Default.Person
    AppIcon.FILES -> Icons.Default.Folder
    AppIcon.NOTES -> Icons.Default.Description
    AppIcon.INTERNET -> Icons.Default.Explore
    AppIcon.MUSIC -> Icons.Default.MusicNote
    AppIcon.WEATHER -> Icons.Default.WbCloudy
    AppIcon.STORE -> Icons.Default.ShoppingBag
    AppIcon.RECORDER -> Icons.Default.Mic
}

@Composable
fun LauncherAppIcon(app: MockApplication, onClick: () -> Unit, modifier: Modifier = Modifier, labelColor: Color = Color.Unspecified) {
    val iconStyle = style(app.icon)
    val context = LocalContext.current
    val actualIcon = remember(app.icon) { installedIcon(context, app.icon) }
    Column(modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(62.dp)
                .shadow(3.dp, RoundedCornerShape(21.dp), ambientColor = iconStyle.bottom.copy(alpha = .28f))
                .clip(RoundedCornerShape(21.dp))
                .background(Brush.linearGradient(listOf(iconStyle.top, iconStyle.bottom))),
            contentAlignment = Alignment.Center,
        ) {
            if (actualIcon != null) {
                Image(actualIcon, tr(app.name), Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            } else if (app.icon == AppIcon.CALENDAR) {
                Column(
                    Modifier.size(width = 37.dp, height = 39.dp).clip(RoundedCornerShape(7.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.fillMaxWidth().height(9.dp).background(Color(0xFF4287E5)))
                    Text("3", color = Color(0xFF315FAE), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Icon(image(app.icon), tr(app.name), Modifier.size(32.dp), tint = iconStyle.glyph)
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(tr(app.name), color = labelColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
