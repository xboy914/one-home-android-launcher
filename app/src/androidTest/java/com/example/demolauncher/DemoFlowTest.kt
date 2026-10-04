package com.example.demolauncher

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.demolauncher.data.datastore.DeviceProfileDataStore
import com.example.demolauncher.data.model.AppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DemoFlowTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Before fun resetLanguage() { runBlocking { val store = DeviceProfileDataStore(rule.activity.applicationContext); store.reset(); store.update(store.profile.first().copy(language = AppLanguage.ENGLISH)) }; rule.waitForIdle() }

    private fun phone() { rule.onAllNodesWithText("Phone")[0].performClick() }
    private fun back() { rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }; rule.waitForIdle() }
    private fun code(value: String) { value.forEach { rule.onNodeWithText(it.toString()).performClick() } }

    @Test fun launcherPagesSwipeBothDirections() {
        rule.onRoot().performTouchInput { swipeLeft() }
        rule.onNodeWithText("More apps").assertIsDisplayed()
        rule.onRoot().performTouchInput { swipeRight() }
        rule.onRoot().performTouchInput { swipeRight() }
        rule.onNodeWithText("Today").assertIsDisplayed()
    }

    @Test fun identifiersFromCode() {
        phone(); code("*#06#")
        rule.onNodeWithText("IMEI(MEID) and S/N").assertIsDisplayed()
    }

    @Test fun editedIdentifiersAppearInDialerAndAboutPhone() {
        phone(); code("*#00#")
        rule.onNodeWithText("Device configuration").assertIsDisplayed()
        rule.onNodeWithText("IMEI 1").performTextClearance()
        rule.onNodeWithText("IMEI 1").performTextInput("111111111111111")
        rule.onNodeWithTag("configList").performScrollToNode(hasText("Save"))
        rule.onNodeWithText("Save").performClick()
        back()
        code("*#06#")
        rule.onNodeWithText("111111111111111", substring = true).assertIsDisplayed()
        back()
        back()
        rule.onNodeWithText("Settings").performClick()
        rule.onNodeWithTag("settingsList").performScrollToNode(hasText("About phone"))
        rule.onNodeWithText("About phone").performClick()
        rule.onNodeWithText("111111111111111", substring = true).assertIsDisplayed()
    }

    @Test fun fakeCallStaysInsideApp() {
        phone(); code("123456")
        rule.onNodeWithContentDescription("Call").performClick()
        rule.onNodeWithText("No phone connection").assertIsDisplayed()
        rule.onNodeWithContentDescription("End call").performClick()
        rule.onNodeWithText("Phone").assertIsDisplayed()
    }

    @Test fun fakeCallEndsAutomaticallyAfterAboutThreeAndHalfSeconds() {
        phone(); code("123456")
        rule.onNodeWithContentDescription("Call").performClick()
        rule.onNodeWithText("No phone connection").assertIsDisplayed()
        rule.waitUntil(6_000) {
            rule.onAllNodesWithText("Phone").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText("Phone").assertIsDisplayed()
    }

    @Test fun languageSwitchUpdatesTheWholeApp() {
        phone(); code("*#00#")
        rule.onNodeWithTag("languagePersian").performClick()
        rule.onNodeWithTag("configList").performScrollToNode(hasText("Save"))
        rule.onNodeWithText("Save").performClick()
        back()
        rule.onNodeWithText("تلفن").assertIsDisplayed()
        rule.onNodeWithText("اخیر").performClick()
        rule.onNodeWithText("برای شروع دانلود کنید").assertIsDisplayed()
        code("*#00#")
        rule.onNodeWithText("پیکربندی دستگاه").assertIsDisplayed()
        rule.onNodeWithTag("languageEnglish").performClick()
        rule.onNodeWithTag("configList").performScrollToNode(hasText("ذخیره"))
        rule.onNodeWithText("ذخیره").performClick()
        back()
        rule.onNodeWithText("Phone").assertIsDisplayed()
    }

    @Test fun cameraIconOpensDeviceCamera() {
        rule.onAllNodesWithText("Camera")[0].performClick()
        rule.waitUntil(5_000) { !rule.activity.hasWindowFocus() }
    }

    @Test fun galleryIconOpensDeviceGallery() {
        rule.onAllNodesWithText("Gallery")[0].performClick()
        rule.waitUntil(5_000) { !rule.activity.hasWindowFocus() }
    }
}
