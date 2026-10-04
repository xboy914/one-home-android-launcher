package com.example.demolauncher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.demolauncher.data.model.MockDeviceProfile
import com.example.demolauncher.data.repository.DeviceProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: DeviceProfileRepository) : ViewModel() {
    val profile = repository.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MockDeviceProfile.Default)
    fun save(value: MockDeviceProfile, result: (Boolean) -> Unit) = viewModelScope.launch {
        result(runCatching { repository.updateProfile(value) }.isSuccess)
    }
    fun reset(result: (Boolean) -> Unit) = viewModelScope.launch { result(runCatching { repository.reset() }.isSuccess) }
    class Factory(private val repository: DeviceProfileRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(repository) as T
    }
}
