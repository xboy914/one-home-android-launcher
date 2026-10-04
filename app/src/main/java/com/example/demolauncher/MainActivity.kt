package com.example.demolauncher

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demolauncher.data.system.DemoQuickSettingsRepository
import com.example.demolauncher.data.system.DemoSystemState
import com.example.demolauncher.data.system.DeviceStatus
import com.example.demolauncher.data.system.DeviceStatusRepository
import com.example.demolauncher.ui.systemui.DemoSystemShell
import androidx.compose.runtime.getValue
import com.example.demolauncher.data.datastore.DeviceProfileDataStore
import com.example.demolauncher.data.repository.DeviceProfileRepositoryImpl
import com.example.demolauncher.ui.navigation.AppNavigation
import com.example.demolauncher.ui.theme.DemoTheme

class MainActivity : ComponentActivity() {
    private val homeRoleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    private val repository by lazy { DeviceProfileRepositoryImpl(DeviceProfileDataStore(applicationContext)) }
    private val deviceStatus by lazy { DeviceStatusRepository(applicationContext) }
    private val demoSystem by lazy { DemoQuickSettingsRepository(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyImmersiveMode()
        setContent {
            val systemState by demoSystem.state.collectAsStateWithLifecycle(initialValue = DemoSystemState())
            val realStatus by deviceStatus.status.collectAsStateWithLifecycle(initialValue = DeviceStatus())
            DemoTheme(systemState.darkModeEnabled) {
                AppNavigation(repository, ::requestHomeRole, systemState, demoSystem, realStatus)
            }
        }
    }
    override fun onResume() { super.onResume(); applyImmersiveMode() }
    override fun onWindowFocusChanged(hasFocus: Boolean) { super.onWindowFocusChanged(hasFocus); if (hasFocus) applyImmersiveMode() }
    private fun applyImmersiveMode() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.navigationBars())
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    private fun requestHomeRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val manager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (manager.isRoleAvailable(RoleManager.ROLE_HOME) && !manager.isRoleHeld(RoleManager.ROLE_HOME)) {
                homeRoleLauncher.launch(manager.createRequestRoleIntent(RoleManager.ROLE_HOME))
            }
        }
    }
}
