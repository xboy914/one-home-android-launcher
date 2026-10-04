package com.example.demolauncher.domain

import org.junit.Assert.*
import org.junit.Test

class SecretCodeParserTest {
    @Test fun codes() {
        assertEquals(SecretCode.ShowDeviceIdentifiers, SecretCodeParser.parse("*#06#"))
        assertEquals(SecretCode.OpenDeviceConfiguration, SecretCodeParser.parse("*#00#"))
        listOf("*#01#", "123456", "", "*", "*#", "*#0", "*#06").forEach { assertNull(SecretCodeParser.parse(it)) }
    }
}
