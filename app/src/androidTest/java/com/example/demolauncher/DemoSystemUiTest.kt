package com.example.demolauncher

import android.view.WindowInsets
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demolauncher.data.system.DemoQuickSettingsRepository
import com.example.demolauncher.data.system.DeviceStatusRepository
import com.example.demolauncher.data.datastore.DeviceProfileDataStore
import com.example.demolauncher.data.model.AppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoSystemUiTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Before fun reset() {
        runBlocking {
            DemoQuickSettingsRepository(rule.activity.applicationContext).reset()
            val profile = DeviceProfileDataStore(rule.activity.applicationContext)
            profile.update(profile.profile.first().copy(language = AppLanguage.PERSIAN))
        }
        rule.waitForIdle()
    }

    @Test fun immersiveStatusAndDemoShade() {
        rule.onNodeWithTag("customStatusBar").assertIsDisplayed()
        val battery = runBlocking { DeviceStatusRepository(rule.activity.applicationContext).status.first { it.batteryPercentage != null }.batteryPercentage }
        val persianBattery = battery.toString().map { "۰۱۲۳۴۵۶۷۸۹"[it - '0'] }.joinToString("")
        rule.onNodeWithTag("statusBattery", useUnmergedTree = true).assertTextEquals("$persianBattery%")
        rule.runOnUiThread {
            val insets = rule.activity.window.decorView.rootWindowInsets
            assertFalse(insets.isVisible(WindowInsets.Type.statusBars()))
            org.junit.Assert.assertTrue(insets.isVisible(WindowInsets.Type.navigationBars()))
        }
        rule.onNodeWithTag("customStatusBar").performTouchInput { swipeDown() }
        rule.onNodeWithTag("quickSettingsShade").assertIsDisplayed()
        rule.onNodeWithTag("tile_WIFI").performClick()
        rule.onAllNodesWithText("Wi-Fi")[0].assertIsDisplayed()
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
        rule.onNodeWithTag("tile_DARK_MODE").performClick()
        rule.onNodeWithTag("shadeSettings").performClick()
        rule.onNodeWithText("تنظیمات").assertIsDisplayed()
        rule.onNodeWithTag("customStatusBar").assertIsDisplayed()
    }

    @Test fun backClosesShadeBeforeNavigation() {
        rule.onNodeWithTag("customStatusBar").performClick()
        rule.onNodeWithTag("quickSettingsShade").assertIsDisplayed()
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
        rule.onNodeWithTag("quickSettingsShade").assertDoesNotExist()
        rule.onNodeWithTag("customStatusBar").assertIsDisplayed()
    }
}
