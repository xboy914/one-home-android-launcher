package com.example.demolauncher.ui.dialer

import androidx.lifecycle.ViewModel
import com.example.demolauncher.domain.SecretCodeParser
import com.example.demolauncher.domain.SecretCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DialerViewModel : ViewModel() {
    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()
    fun append(value: String): SecretCode? {
        _input.value += value
        return SecretCodeParser.parse(_input.value)?.also { _input.value = "" }
    }
    fun backspace() { _input.value = _input.value.dropLast(1) }
    fun clear() { _input.value = "" }
}
