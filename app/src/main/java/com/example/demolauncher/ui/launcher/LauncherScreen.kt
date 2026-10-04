package com.example.demolauncher.ui.launcher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.example.demolauncher.R
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demolauncher.data.model.AppIcon
import com.example.demolauncher.data.model.MockApplication
import com.example.demolauncher.ui.localization.tr

private val lavender = Color(0xFFB784E8)
private val violet = Color(0xFF655BC6)
private val ink = Color.White

@Composable
fun LauncherScreen(viewModel: LauncherViewModel, onOpen: (MockApplication) -> Unit, onDrawer: () -> Unit) {
    val clock by rememberSystemClock()
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val dark = MaterialTheme.colorScheme.background == Color(0xFF11131B)
    val wallpaper = if (dark) listOf(Color(0xFF44316F), Color(0xFF332A67), Color(0xFF171B3F))
        else listOf(lavender, Color(0xFF9B70DD), violet)
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(wallpaper)).pointerInput(onDrawer) {
        var drag = 0f
        detectVerticalDragGestures(
            onVerticalDrag = { _, amount -> drag += amount },
            onDragEnd = { if (drag < -70f) onDrawer(); drag = 0f },
            onDragCancel = { drag = 0f },
        )
    }) {
        Canvas(Modifier.fillMaxSize()) {
            drawOval(Color(0xFF503EAE).copy(alpha = .24f), Offset(size.width * .12f, size.height * .10f), Size(size.width * .85f, size.height * .48f))
            drawOval(Color(0xFFECC2FA).copy(alpha = .20f), Offset(size.width * .28f, size.height * .25f), Size(size.width * .65f, size.height * .40f))
            drawCircle(Color(0xFF302F93).copy(alpha = .18f), radius = size.width * .48f, center = Offset(size.width * .18f, size.height * .86f))
        }
        Column(Modifier.fillMaxSize()) {
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f), pageSpacing = 8.dp) { page ->
                when (page) {
                    0 -> TodayPage(clock)
                    1 -> HomePage(viewModel.apps.filter { it.icon in listOf(AppIcon.CLOCK, AppIcon.WEATHER, AppIcon.FILES, AppIcon.SETTINGS) }, onOpen)
                    else -> AppsPage(viewModel.apps.drop(8), onOpen, onDrawer)
                }
            }
            Row(Modifier.fillMaxWidth().padding(bottom = 19.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                repeat(3) { index ->
                    Box(Modifier.padding(horizontal = 4.dp).size(if (index == pagerState.currentPage) 7.dp else 5.dp)
                        .background(if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = .52f), CircleShape))
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                viewModel.dock.forEach { app -> LauncherAppIcon(app, { onOpen(app) }, Modifier.width(74.dp), Color.White) }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun HomePage(apps: List<MockApplication>, onOpen: (MockApplication) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 19.dp)) {
        Spacer(Modifier.height(42.dp))
        Box(Modifier.fillMaxWidth().height(145.dp).background(Brush.linearGradient(listOf(Color(0xFFC68BFA).copy(alpha = .92f), Color(0xFFE1A9EF).copy(alpha = .85f))), RoundedCornerShape(30.dp))) {
            Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
                Text("🎂", fontSize = 29.sp)
                Spacer(Modifier.height(7.dp))
                Text(tr("Jenny's birthday!"), color = ink, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(tr("Send a card or gift to celebrate."), color = ink.copy(alpha = .82f), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(13.dp))
        Row(Modifier.fillMaxWidth().height(126.dp), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            Image(
                painter = painterResource(R.drawable.ice_cream_widget),
                contentDescription = tr("Ice cream photo"),
                modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth().weight(1f).background(Color.White.copy(alpha = .72f), RoundedCornerShape(25.dp)).padding(horizontal = 17.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("Start"), Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = Color(0xFF34234F), fontSize = 15.sp)
                    Text("🌈", fontSize = 29.sp)
                }
                Row(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF48A9F4), RoundedCornerShape(25.dp)).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌤", fontSize = 28.sp)
                    Spacer(Modifier.width(7.dp))
                    Column {
                        Text("24°", color = ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Text(tr("Seoul"), color = ink.copy(alpha = .86f), fontSize = 10.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(13.dp))
        Row(Modifier.fillMaxWidth().background(Color.White.copy(alpha = .52f), RoundedCornerShape(28.dp)).padding(vertical = 9.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(100, 53, 15, 50).forEachIndexed { index, value ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(progress = { value / 100f }, modifier = Modifier.size(30.dp), color = if (index == 2) Color(0xFFE77485) else Color(0xFF1FCB82), strokeWidth = 4.dp, trackColor = Color.White.copy(alpha = .6f))
                        Text(listOf("⌚", "♡", "✎", "♫")[index], fontSize = 11.sp, color = Color(0xFF293052))
                    }
                    Text("$value", fontSize = 10.sp, color = Color(0xFF293052))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            apps.forEach { app -> LauncherAppIcon(app, { onOpen(app) }, Modifier.width(75.dp), Color.White) }
        }
        Spacer(Modifier.height(13.dp))
    }
}

@Composable
private fun TodayPage(clock: SystemClock) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(52.dp))
        Text(clock.time, color = ink, fontSize = 50.sp, fontWeight = FontWeight.Light)
        Text(clock.date, color = ink.copy(alpha = .82f), fontSize = 15.sp)
        Spacer(Modifier.height(25.dp))
        Surface(shape = RoundedCornerShape(30.dp), color = Color.White.copy(alpha = .30f)) {
            Column(Modifier.fillMaxWidth().padding(22.dp)) {
                Text(tr("Today"), color = ink, fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                Text(tr("Your favorite apps, all in one place."), color = ink.copy(alpha = .82f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun AppsPage(apps: List<MockApplication>, onOpen: (MockApplication) -> Unit, onDrawer: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(50.dp))
        Text(tr("More apps"), color = ink, fontSize = 32.sp, fontWeight = FontWeight.Light)
        Spacer(Modifier.height(22.dp))
        apps.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(bottom = 18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                row.forEach { app -> LauncherAppIcon(app, { onOpen(app) }, Modifier.width(74.dp), Color.White) }
                repeat(4 - row.size) { Spacer(Modifier.width(74.dp)) }
            }
        }
        OutlinedButton(onClick = onDrawer, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.Search, null)
            Spacer(Modifier.width(8.dp))
            Text(tr("All apps"))
        }
    }
}
