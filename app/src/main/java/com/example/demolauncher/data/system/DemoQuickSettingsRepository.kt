package com.example.demolauncher.data.system

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.demoSystemStore by preferencesDataStore("demo_system_state")

class DemoQuickSettingsRepository(private val context: Context) {
    private val flags = mapOf(
        DemoToggle.WIFI to booleanPreferencesKey("wifi"),
        DemoToggle.MOBILE_DATA to booleanPreferencesKey("mobile_data"),
        DemoToggle.BLUETOOTH to booleanPreferencesKey("bluetooth"),
        DemoToggle.AIRPLANE to booleanPreferencesKey("airplane"),
        DemoToggle.FLASHLIGHT to booleanPreferencesKey("flashlight"),
        DemoToggle.AUTO_ROTATE to booleanPreferencesKey("auto_rotate"),
        DemoToggle.DARK_MODE to booleanPreferencesKey("dark_mode"),
        DemoToggle.DND to booleanPreferencesKey("dnd"),
        DemoToggle.LOCATION to booleanPreferencesKey("location"),
        DemoToggle.BATTERY_SAVER to booleanPreferencesKey("battery_saver"),
    )
    private val brightnessKey = floatPreferencesKey("brightness")
    private val volumeKey = floatPreferencesKey("volume")
    private val clearedKey = stringSetPreferencesKey("cleared_notifications")
    private val defaults = DemoSystemState()

    val state: Flow<DemoSystemState> = context.demoSystemStore.data.catch {
        if (it is IOException) emit(emptyPreferences()) else throw it
    }.map { preferences ->
        defaults.copy(
            wifiEnabled = preferences[flags.getValue(DemoToggle.WIFI)] ?: defaults.wifiEnabled,
            mobileDataEnabled = preferences[flags.getValue(DemoToggle.MOBILE_DATA)] ?: defaults.mobileDataEnabled,
            bluetoothEnabled = preferences[flags.getValue(DemoToggle.BLUETOOTH)] ?: defaults.bluetoothEnabled,
            airplaneModeEnabled = preferences[flags.getValue(DemoToggle.AIRPLANE)] ?: defaults.airplaneModeEnabled,
            flashlightEnabled = preferences[flags.getValue(DemoToggle.FLASHLIGHT)] ?: defaults.flashlightEnabled,
            autoRotateEnabled = preferences[flags.getValue(DemoToggle.AUTO_ROTATE)] ?: defaults.autoRotateEnabled,
            darkModeEnabled = preferences[flags.getValue(DemoToggle.DARK_MODE)] ?: defaults.darkModeEnabled,
            doNotDisturbEnabled = preferences[flags.getValue(DemoToggle.DND)] ?: defaults.doNotDisturbEnabled,
            locationEnabled = preferences[flags.getValue(DemoToggle.LOCATION)] ?: defaults.locationEnabled,
            batterySaverEnabled = preferences[flags.getValue(DemoToggle.BATTERY_SAVER)] ?: defaults.batterySaverEnabled,
            brightness = (preferences[brightnessKey] ?: defaults.brightness).coerceIn(0f, 1f),
            volume = (preferences[volumeKey] ?: defaults.volume).coerceIn(0f, 1f),
            notifications = defaults.notifications.filterNot { it.id in (preferences[clearedKey] ?: emptySet()) },
        )
    }

    suspend fun reset() { context.demoSystemStore.edit { it.clear() } }

    suspend fun toggle(toggle: DemoToggle) {
        context.demoSystemStore.edit { prefs ->
            val key = flags.getValue(toggle)
            val value = prefs[key] ?: enabled(defaults, toggle)
            prefs[key] = !value
            if (toggle == DemoToggle.AIRPLANE && !value) {
                prefs[flags.getValue(DemoToggle.WIFI)] = false
                prefs[flags.getValue(DemoToggle.MOBILE_DATA)] = false
            } else if ((toggle == DemoToggle.WIFI || toggle == DemoToggle.MOBILE_DATA) && !value) {
                prefs[flags.getValue(DemoToggle.AIRPLANE)] = false
            }
        }
    }
    suspend fun setBrightness(value: Float) { context.demoSystemStore.edit { it[brightnessKey] = value.coerceIn(0f, 1f) } }
    suspend fun setVolume(value: Float) { context.demoSystemStore.edit { it[volumeKey] = value.coerceIn(0f, 1f) } }
    suspend fun clearNotifications() { context.demoSystemStore.edit { it[clearedKey] = defaults.notifications.filter { n -> n.isClearable }.map { n -> n.id }.toSet() } }
}

fun enabled(state: DemoSystemState, toggle: DemoToggle): Boolean = when (toggle) {
    DemoToggle.WIFI -> state.wifiEnabled
    DemoToggle.MOBILE_DATA -> state.mobileDataEnabled
    DemoToggle.BLUETOOTH -> state.bluetoothEnabled
    DemoToggle.AIRPLANE -> state.airplaneModeEnabled
    DemoToggle.FLASHLIGHT -> state.flashlightEnabled
    DemoToggle.AUTO_ROTATE -> state.autoRotateEnabled
    DemoToggle.DARK_MODE -> state.darkModeEnabled
    DemoToggle.DND -> state.doNotDisturbEnabled
    DemoToggle.LOCATION -> state.locationEnabled
    DemoToggle.BATTERY_SAVER -> state.batterySaverEnabled
}
