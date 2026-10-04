package com.example.demolauncher.ui.launcher

import com.example.demolauncher.ui.localization.tr
import com.example.demolauncher.ui.localization.LocalAppLanguage
import com.example.demolauncher.ui.localization.translate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demolauncher.data.model.MockApplication

@Composable fun AppDrawerScreen(viewModel: LauncherViewModel, onOpen: (MockApplication) -> Unit) {
    var query by remember { mutableStateOf("") }
    val language = LocalAppLanguage.current
    Column(Modifier.fillMaxSize().padding(top = 40.dp)) {
        Text(tr("Apps"), Modifier.padding(24.dp), style = MaterialTheme.typography.headlineLarge)
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth().padding(horizontal = 24.dp), placeholder = { Text(tr("Search apps")) }, singleLine = true, shape = RoundedCornerShape(24.dp))
        LazyVerticalGrid(GridCells.Fixed(4), Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(viewModel.apps.filter { (it.name.contains(query, ignoreCase = true) || translate(it.name, language).contains(query, ignoreCase = true)) }, key = { it.id }) { app -> LauncherAppIcon(app, { onOpen(app) }) }
        }
    }
}
