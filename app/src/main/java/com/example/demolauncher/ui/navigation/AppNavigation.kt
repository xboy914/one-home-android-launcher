package com.example.demolauncher.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.demolauncher.data.model.AppLanguage
import com.example.demolauncher.ui.localization.LocalAppLanguage
import com.example.demolauncher.ui.localization.tr
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.demolauncher.data.model.AppAction
import com.example.demolauncher.data.model.MockApplication
import com.example.demolauncher.data.repository.DeviceProfileRepository
import com.example.demolauncher.data.system.DemoQuickSettingsRepository
import com.example.demolauncher.data.system.DemoSystemState
import com.example.demolauncher.data.system.DeviceStatus
import com.example.demolauncher.ui.systemui.DemoSystemShell
import com.example.demolauncher.domain.SecretCode
import com.example.demolauncher.ui.dialer.*
import com.example.demolauncher.ui.launcher.*
import com.example.demolauncher.ui.settings.*
import kotlinx.coroutines.launch

@Composable fun AppNavigation(repository: DeviceProfileRepository, requestHomeRole: () -> Unit, systemState: DemoSystemState, systemRepository: DemoQuickSettingsRepository, realStatus: DeviceStatus) {
    val nav = rememberNavController()
    val context = LocalContext.current
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val launcherViewModel: LauncherViewModel = viewModel()
    val dialerViewModel: DialerViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository))
    val profile by settingsViewModel.profile.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMessage = com.example.demolauncher.ui.localization.translate("Download to get started", profile.language)
    val openApp: (MockApplication) -> Unit = { app ->
        when (app.action) {
            AppAction.PHONE -> nav.navigate(Routes.Dialer) { popUpTo(Routes.Launcher) }
            AppAction.SETTINGS -> nav.navigate(Routes.Settings) { popUpTo(Routes.Launcher) }
            AppAction.CAMERA -> if (!SystemMediaApps.openCamera(context)) scope.launch { snackbar.showSnackbar("Camera app not available") }
            AppAction.GALLERY -> if (!SystemMediaApps.openGallery(context)) scope.launch { snackbar.showSnackbar("Gallery app not available") }
            AppAction.DEMO_ONLY -> scope.launch { snackbar.showSnackbar(demoMessage) }
        }
    }
    CompositionLocalProvider(
        LocalAppLanguage provides profile.language,
        LocalLayoutDirection provides if (profile.language == AppLanguage.PERSIAN) LayoutDirection.Rtl else LayoutDirection.Ltr,
    ) {
    DemoSystemShell(
        state = systemState,
        deviceStatus = realStatus,
        onToggle = { toggle -> scope.launch { systemRepository.toggle(toggle) } },
        onBrightness = { value -> scope.launch { systemRepository.setBrightness(value) } },
        onVolume = { value -> scope.launch { systemRepository.setVolume(value) } },
        onClear = { scope.launch { systemRepository.clearNotifications() } },
        onSettings = { nav.navigate(Routes.Settings) { launchSingleTop = true } },
        onRestart = { nav.navigate(Routes.Launcher) { popUpTo(Routes.Launcher) { inclusive = false }; launchSingleTop = true } },
        phoneNumber = profile.phoneNumber,
        specialDarkStatus = currentRoute == Routes.DeviceIdentifiers || currentRoute == Routes.FakeCall,
        isLauncher = currentRoute == Routes.Launcher,
    ) {
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { _ ->
        NavHost(navController = nav, startDestination = Routes.Launcher,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(240)) },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(180)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(220)) }) {
            composable(Routes.Launcher) { LauncherScreen(launcherViewModel, openApp, { nav.navigate(Routes.AppDrawer) }) }
            composable(Routes.AppDrawer) { AppDrawerScreen(launcherViewModel, openApp) }
            composable(Routes.Dialer) { DialerScreen(dialerViewModel, { code -> nav.navigate(when (code) {
                SecretCode.ShowDeviceIdentifiers -> Routes.DeviceIdentifiers
                SecretCode.OpenDeviceConfiguration -> Routes.DeviceConfig
            }) }, { number -> nav.navigate(Routes.fakeCall(number)) }) }
            composable(Routes.FakeCall, arguments = listOf(navArgument("number") { type = NavType.StringType })) { entry -> FakeCallingScreen(entry.arguments?.getString("number").orEmpty(), { nav.popBackStack() }) }
            composable(Routes.DeviceIdentifiers) { DeviceIdentifiersScreen(profile, { nav.popBackStack() }) }
            composable(Routes.DeviceConfig) { DeviceConfigScreen(profile, { value, result -> settingsViewModel.save(value, result) }, { result -> settingsViewModel.reset(result) }) }
            composable(Routes.Settings) { SettingsScreen(profile.deviceName, { nav.navigate(Routes.AboutPhone) }, requestHomeRole) }
            composable(Routes.AboutPhone) { AboutPhoneScreen(profile, { nav.popBackStack() }) }
        }
    }
    }
    }
}
