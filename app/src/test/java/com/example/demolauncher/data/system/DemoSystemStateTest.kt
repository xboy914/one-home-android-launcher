package com.example.demolauncher.data.system

import org.junit.Assert.*
import org.junit.Test

class DemoSystemStateTest {
    @Test fun wifiAndBluetoothToggleOnlyDemoFlags() {
        val initial = DemoSystemState()
        val wifiOff = initial.toggle(DemoToggle.WIFI)
        assertFalse(wifiOff.wifiEnabled)
        assertTrue(wifiOff.mobileDataEnabled)
        assertTrue(wifiOff.bluetoothEnabled)
        assertFalse(wifiOff.toggle(DemoToggle.BLUETOOTH).bluetoothEnabled)
    }

    @Test fun airplaneModeDisablesDemoRadios() {
        val initial = DemoSystemState()
        val result = initial.toggle(DemoToggle.AIRPLANE)
        assertTrue(result.airplaneModeEnabled)
        assertFalse(result.wifiEnabled)
        assertFalse(result.mobileDataEnabled)
        assertFalse(result.toggle(DemoToggle.AIRPLANE).airplaneModeEnabled)
    }

    @Test fun darkModeChangesOnlyThemeFlag() {
        val initial = DemoSystemState()
        assertEquals(initial.copy(darkModeEnabled = true), initial.toggle(DemoToggle.DARK_MODE))
    }
}
