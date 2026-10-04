package com.example.demolauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(primary = Color(0xFF3D64C8), secondary = Color(0xFF6576A8), background = Color(0xFFF7F8FC), surface = Color.White)
private val Dark = darkColorScheme(primary = Color(0xFFA8C1FF), secondary = Color(0xFFB4C3F4), background = Color(0xFF11131B), surface = Color(0xFF202330))

@Composable fun DemoTheme(darkMode: Boolean = false, content: @Composable () -> Unit) = MaterialTheme(colorScheme = if (darkMode) Dark else Light, content = content)
