package com.example.demolauncher.data.datastore

import com.example.demolauncher.data.model.MockDeviceProfile
import kotlinx.coroutines.flow.Flow

interface ProfileStore {
    val profile: Flow<MockDeviceProfile>
    suspend fun update(profile: MockDeviceProfile)
    suspend fun reset()
}
