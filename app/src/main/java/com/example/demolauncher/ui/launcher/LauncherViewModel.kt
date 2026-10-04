package com.example.demolauncher.ui.launcher

import androidx.lifecycle.ViewModel
import com.example.demolauncher.data.model.MockApplications

class LauncherViewModel : ViewModel() { val apps = MockApplications.all; val dock = MockApplications.dock }
