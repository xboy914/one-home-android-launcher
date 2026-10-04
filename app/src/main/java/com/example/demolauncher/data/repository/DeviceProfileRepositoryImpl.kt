package com.example.demolauncher.data.repository

import com.example.demolauncher.data.datastore.ProfileStore
import com.example.demolauncher.data.model.MockDeviceProfile
import kotlinx.coroutines.flow.Flow

class DeviceProfileRepositoryImpl(private val store: ProfileStore) : DeviceProfileRepository {
    override val profile: Flow<MockDeviceProfile> = store.profile
    override suspend fun updateProfile(profile: MockDeviceProfile) = store.update(profile)
    override suspend fun reset() = store.reset()
}
