package com.example.demolauncher

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demolauncher.data.datastore.DeviceProfileDataStore
import com.example.demolauncher.data.model.AppLanguage
import com.example.demolauncher.data.repository.DeviceProfileRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfilePersistenceTest {
    @Test fun profileSurvivesRepositoryRecreation() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val first = DeviceProfileRepositoryImpl(DeviceProfileDataStore(context))
        first.reset()
        try {
            first.updateProfile(first.profile.first().copy(imei1 = "111111111111111", imei2 = "222222222222222", serialNumber = "DEMO123456", language = AppLanguage.PERSIAN))
            val recreated = DeviceProfileRepositoryImpl(DeviceProfileDataStore(context))
            val profile = recreated.profile.first()
            assertEquals("111111111111111", profile.imei1)
            assertEquals("222222222222222", profile.imei2)
            assertEquals("DEMO123456", profile.serialNumber)
            assertEquals(AppLanguage.PERSIAN, profile.language)
        } finally { first.reset() }
    }
}
