package com.example.demolauncher

import androidx.test.core.app.ApplicationProvider
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demolauncher.data.system.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoSystemPersistenceTest {
    @Test fun togglesAndSlidersSurviveRepositoryRecreation() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val first = DemoQuickSettingsRepository(context)
        first.reset()
        try {
            first.toggle(DemoToggle.WIFI)
            first.toggle(DemoToggle.BLUETOOTH)
            first.toggle(DemoToggle.AIRPLANE)
            first.toggle(DemoToggle.DARK_MODE)
            first.setBrightness(.2f)
            first.setVolume(.4f)
            val restored = DemoQuickSettingsRepository(context).state.first()
            assertTrue(restored.airplaneModeEnabled)
            assertFalse(restored.wifiEnabled)
            assertFalse(restored.mobileDataEnabled)
            assertFalse(restored.bluetoothEnabled)
            assertTrue(restored.darkModeEnabled)
            assertEquals(.2f, restored.brightness, .001f)
            assertEquals(.4f, restored.volume, .001f)
        } finally { first.reset() }
    }
}
