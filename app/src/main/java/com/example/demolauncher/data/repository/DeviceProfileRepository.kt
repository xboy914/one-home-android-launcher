package com.example.demolauncher.data.repository

import com.example.demolauncher.data.model.MockDeviceProfile
import kotlinx.coroutines.flow.Flow

interface DeviceProfileRepository {
    val profile: Flow<MockDeviceProfile>
    suspend fun updateProfile(profile: MockDeviceProfile)
    suspend fun reset()
}
