package com.example.demolauncher.data.model

data class MockDeviceProfile(
    val imei1: String = "000000000000000",
    val imei2: String = "111111111111111",
    val serialNumber: String = "DEMO0000001",
    val deviceName: String = "Galaxy A36 5G",
    val modelName: String = "Galaxy A36 5G",
    val modelNumber: String = "SM-A366B/DS",
    val phoneNumber: String = "+10000000000",
    val androidVersion: String = "16",
    val uiVersion: String = "One Style UI 1.0",
    val buildNumber: String = "SAMPLE.2026.001",
    val storageText: String = "256 GB",
    val securityPatch: String = "October 1, 2026",
    val language: AppLanguage = AppLanguage.PERSIAN,
) {
    companion object { val Default = MockDeviceProfile() }
}
