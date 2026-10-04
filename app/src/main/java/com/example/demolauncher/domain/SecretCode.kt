package com.example.demolauncher.domain

sealed interface SecretCode {
    data object ShowDeviceIdentifiers : SecretCode
    data object OpenDeviceConfiguration : SecretCode
}
