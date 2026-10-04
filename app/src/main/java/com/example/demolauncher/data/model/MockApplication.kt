package com.example.demolauncher.data.model

enum class AppAction { PHONE, SETTINGS, CAMERA, GALLERY, DEMO_ONLY }
enum class AppIcon { PHONE, MESSAGES, CAMERA, GALLERY, SETTINGS, CLOCK, CALENDAR, CALCULATOR, CONTACTS, FILES, NOTES, INTERNET, MUSIC, WEATHER, STORE, RECORDER }
data class MockApplication(val id: String, val name: String, val icon: AppIcon, val action: AppAction)

object MockApplications {
    val all = AppIcon.entries.map { icon ->
        val name = icon.name.lowercase().replaceFirstChar { it.uppercase() }
        MockApplication(icon.name.lowercase(), name, icon, when (icon) {
            AppIcon.PHONE -> AppAction.PHONE
            AppIcon.SETTINGS -> AppAction.SETTINGS
            AppIcon.CAMERA -> AppAction.CAMERA
            AppIcon.GALLERY -> AppAction.GALLERY
            else -> AppAction.DEMO_ONLY
        })
    }
    val dock = listOf(AppIcon.PHONE, AppIcon.GALLERY, AppIcon.INTERNET, AppIcon.CAMERA).map { icon -> all.first { it.icon == icon } }
}
