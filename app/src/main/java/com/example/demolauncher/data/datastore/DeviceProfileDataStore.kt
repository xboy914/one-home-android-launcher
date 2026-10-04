package com.example.demolauncher.data.datastore

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.demolauncher.data.model.MockDeviceProfile
import com.example.demolauncher.data.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.profileStore by preferencesDataStore(
    name = "demo_device_profile",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

class DeviceProfileDataStore(private val context: Context) : ProfileStore {
    private val keys = listOf("imei1", "imei2", "serialNumber", "deviceName", "modelName", "modelNumber", "phoneNumber", "androidVersion", "uiVersion", "buildNumber", "storageText", "securityPatch", "language").map(::stringPreferencesKey)
    override val profile: Flow<MockDeviceProfile> = context.profileStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { p ->
            val d = MockDeviceProfile.Default
            MockDeviceProfile(
                p[keys[0]] ?: d.imei1, p[keys[1]] ?: d.imei2,
                useCurrentDefault(p[keys[2]], "RF8X20DEMO1", d.serialNumber),
                useCurrentDefault(p[keys[3]], "Galaxy Demo", d.deviceName),
                useCurrentDefault(p[keys[4]], "Demo S Series", d.modelName),
                useCurrentDefault(p[keys[5]], "SM-DEMO01", d.modelNumber),
                p[keys[6]] ?: d.phoneNumber, p[keys[7]] ?: d.androidVersion,
                useCurrentDefault(p[keys[8]], "Demo UI 1.0", d.uiVersion),
                useCurrentDefault(p[keys[9]], "DEMO.2026.001", d.buildNumber),
                p[keys[10]] ?: d.storageText, p[keys[11]] ?: d.securityPatch,
                AppLanguage.fromStored(p[keys[12]]),
            )
        }
        .catch { emit(MockDeviceProfile.Default) }

    override suspend fun update(profile: MockDeviceProfile) {
        context.profileStore.edit { p ->
            values(profile).forEachIndexed { index, value -> p[keys[index]] = value }
        }
    }
    override suspend fun reset() { context.profileStore.edit { it.clear() } }
    private fun useCurrentDefault(stored: String?, previousDefault: String, currentDefault: String) =
        if (stored == null || stored == previousDefault) currentDefault else stored

    private fun values(p: MockDeviceProfile) = listOf(p.imei1, p.imei2, p.serialNumber, p.deviceName, p.modelName, p.modelNumber, p.phoneNumber, p.androidVersion, p.uiVersion, p.buildNumber, p.storageText, p.securityPatch, p.language.storedValue)
}
