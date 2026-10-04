package com.example.demolauncher.data.model

enum class AppLanguage(val storedValue: String) {
    ENGLISH("en"),
    PERSIAN("fa");

    companion object {
        fun fromStored(value: String?): AppLanguage = entries.firstOrNull { it.storedValue == value } ?: ENGLISH
    }
}
