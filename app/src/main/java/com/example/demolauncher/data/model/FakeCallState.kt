package com.example.demolauncher.data.model

data class FakeCallState(val number: String, val connectedSeconds: Int = 0, val connected: Boolean = false, val muted: Boolean = false, val speaker: Boolean = false, val keypad: Boolean = false, val addCall: Boolean = false, val video: Boolean = false, val bluetooth: Boolean = false)
