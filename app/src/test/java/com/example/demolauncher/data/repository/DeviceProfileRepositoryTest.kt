package com.example.demolauncher.data.repository

import com.example.demolauncher.data.datastore.ProfileStore
import com.example.demolauncher.data.model.MockDeviceProfile
import com.example.demolauncher.data.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceProfileRepositoryTest {
    private class MemoryStore : ProfileStore {
        override val profile = MutableStateFlow(MockDeviceProfile.Default)
        override suspend fun update(profile: MockDeviceProfile) { this.profile.value = profile }
        override suspend fun reset() { profile.value = MockDeviceProfile.Default }
    }
    @Test fun updatesAndResetFlow() = runBlocking {
        val repo = DeviceProfileRepositoryImpl(MemoryStore())
        repo.updateProfile(repo.profile.first().copy(imei1 = "111111111111111"))
        assertEquals("111111111111111", repo.profile.first().imei1)
        repo.updateProfile(repo.profile.first().copy(imei2 = "222222222222222", serialNumber = "DEMO123456"))
        assertEquals("222222222222222", repo.profile.first().imei2)
        assertEquals("DEMO123456", repo.profile.first().serialNumber)
        repo.updateProfile(repo.profile.first().copy(language = AppLanguage.PERSIAN))
        assertEquals(AppLanguage.PERSIAN, repo.profile.first().language)
        repo.reset()
        assertEquals(MockDeviceProfile.Default, repo.profile.first())
    }
}
