package com.example.demolauncher.ui.navigation

import android.net.Uri

object Routes {
    const val Launcher = "launcher"
    const val AppDrawer = "appDrawer"
    const val Dialer = "dialer"
    const val FakeCall = "fakeCall/{number}"
    fun fakeCall(number: String) = "fakeCall/${Uri.encode(number)}"
    const val DeviceIdentifiers = "deviceIdentifiers"
    const val DeviceConfig = "deviceConfig"
    const val Settings = "settings"
    const val AboutPhone = "aboutPhone"
}
