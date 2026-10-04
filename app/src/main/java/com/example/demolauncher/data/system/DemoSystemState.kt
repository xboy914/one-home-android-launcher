package com.example.demolauncher.data.system

data class DemoNotification(
    val id: String,
    val appName: String,
    val title: String,
    val message: String,
    val time: String = "Now",
    val isClearable: Boolean = true,
)

data class DemoSystemState(
    val wifiEnabled: Boolean = true,
    val mobileDataEnabled: Boolean = true,
    val bluetoothEnabled: Boolean = true,
    val airplaneModeEnabled: Boolean = false,
    val flashlightEnabled: Boolean = false,
    val autoRotateEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val doNotDisturbEnabled: Boolean = false,
    val locationEnabled: Boolean = true,
    val batterySaverEnabled: Boolean = false,
    val brightness: Float = .65f,
    val volume: Float = .70f,
    val notifications: List<DemoNotification> = listOf(
        DemoNotification("system", "Demo System", "Device simulation is running", "Your demo environment is ready"),
        DemoNotification("messages", "Messages", "2 new demo messages", "Open the demo Messages app"),
        DemoNotification("update", "Software update", "Demo system is up to date", "No updates available"),
    ),
)

enum class DemoToggle { WIFI, MOBILE_DATA, BLUETOOTH, AIRPLANE, FLASHLIGHT, AUTO_ROTATE, DARK_MODE, DND, LOCATION, BATTERY_SAVER }

fun DemoSystemState.toggle(toggle: DemoToggle): DemoSystemState = when (toggle) {
    DemoToggle.WIFI -> copy(wifiEnabled = !wifiEnabled, airplaneModeEnabled = if (!wifiEnabled) false else airplaneModeEnabled)
    DemoToggle.MOBILE_DATA -> copy(mobileDataEnabled = !mobileDataEnabled, airplaneModeEnabled = if (!mobileDataEnabled) false else airplaneModeEnabled)
    DemoToggle.BLUETOOTH -> copy(bluetoothEnabled = !bluetoothEnabled)
    DemoToggle.AIRPLANE -> if (airplaneModeEnabled) copy(airplaneModeEnabled = false) else copy(airplaneModeEnabled = true, wifiEnabled = false, mobileDataEnabled = false)
    DemoToggle.FLASHLIGHT -> copy(flashlightEnabled = !flashlightEnabled)
    DemoToggle.AUTO_ROTATE -> copy(autoRotateEnabled = !autoRotateEnabled)
    DemoToggle.DARK_MODE -> copy(darkModeEnabled = !darkModeEnabled)
    DemoToggle.DND -> copy(doNotDisturbEnabled = !doNotDisturbEnabled)
    DemoToggle.LOCATION -> copy(locationEnabled = !locationEnabled)
    DemoToggle.BATTERY_SAVER -> copy(batterySaverEnabled = !batterySaverEnabled)
}
