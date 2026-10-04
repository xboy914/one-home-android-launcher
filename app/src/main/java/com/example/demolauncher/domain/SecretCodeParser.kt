package com.example.demolauncher.domain

object SecretCodeParser {
    fun parse(input: String): SecretCode? = when (input) {
        "*#06#" -> SecretCode.ShowDeviceIdentifiers
        "*#00#" -> SecretCode.OpenDeviceConfiguration
        else -> null
    }
}
