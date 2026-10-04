package com.example.demolauncher.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.demolauncher.data.model.MockDeviceProfile
import com.example.demolauncher.data.model.AppLanguage
import com.example.demolauncher.ui.localization.tr
import com.example.demolauncher.ui.localization.translate
import kotlinx.coroutines.launch

@Composable fun DeviceConfigScreen(profile: MockDeviceProfile, onSave: (MockDeviceProfile, (Boolean) -> Unit) -> Unit, onReset: ((Boolean) -> Unit) -> Unit) {
    var draft by remember(profile) { mutableStateOf(profile) }
    var confirmReset by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMessage = translate("Device information updated", draft.language)
    val saveError = tr("Could not save device information")
    val resetMessage = translate("Defaults restored", AppLanguage.ENGLISH)
    val resetError = tr("Could not reset device information")
    val valid = draft.imei1.length in 1..15 && draft.imei1.all(Char::isDigit) && draft.imei2.length in 1..15 && draft.imei2.all(Char::isDigit) && draft.serialNumber.isNotBlank() && draft.deviceName.isNotBlank()
    if (confirmReset) AlertDialog(onDismissRequest = { confirmReset = false }, title = { Text(tr("Reset all simulated device information?")) }, confirmButton = { TextButton(onClick = { confirmReset = false; onReset { success -> scope.launch { snackbar.showSnackbar(if (success) resetMessage else resetError) } } }) { Text(tr("Reset")) } }, dismissButton = { TextButton(onClick = { confirmReset = false }) { Text(tr("Cancel")) } })
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).safeDrawingPadding().padding(horizontal = 20.dp).testTag("configList"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Spacer(Modifier.height(18.dp)); Text(tr("Device configuration"), style = MaterialTheme.typography.headlineMedium); Text(tr("All values below are simulated and stored locally."), style = MaterialTheme.typography.bodyMedium) }
            item { LanguagePicker(draft.language) { draft = draft.copy(language = it) } }
            item { Field("IMEI 1", draft.imei1, KeyboardType.Number, { draft = draft.copy(imei1 = it.filter(Char::isDigit).take(15)) }) }
            item { Field("IMEI 2", draft.imei2, KeyboardType.Number, { draft = draft.copy(imei2 = it.filter(Char::isDigit).take(15)) }) }
            item { Field("Serial Number", draft.serialNumber, onChange = { draft = draft.copy(serialNumber = it) }) }
            item { Field("Device Name", draft.deviceName, onChange = { draft = draft.copy(deviceName = it) }) }
            item { Field("Model Name", draft.modelName, onChange = { draft = draft.copy(modelName = it) }) }
            item { Field("Model Number", draft.modelNumber, onChange = { draft = draft.copy(modelNumber = it) }) }
            item { Field("Phone Number", draft.phoneNumber, onChange = { draft = draft.copy(phoneNumber = it) }) }
            item { Field("Android Version", draft.androidVersion, onChange = { draft = draft.copy(androidVersion = it) }) }
            item { Field("UI Version", draft.uiVersion, onChange = { draft = draft.copy(uiVersion = it) }) }
            item { Field("Build Number", draft.buildNumber, onChange = { draft = draft.copy(buildNumber = it) }) }
            item { Field("Storage", draft.storageText, onChange = { draft = draft.copy(storageText = it) }) }
            item { Field("Security Patch", draft.securityPatch, onChange = { draft = draft.copy(securityPatch = it) }) }
            item { Button(onClick = { onSave(draft) { success -> scope.launch { snackbar.showSnackbar(if (success) savedMessage else saveError) } } }, enabled = valid, modifier = Modifier.fillMaxWidth()) { Text(tr("Save")) } }
            item { OutlinedButton(onClick = { confirmReset = true }, modifier = Modifier.fillMaxWidth()) { Text(tr("Reset to defaults")) }; Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable private fun Field(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, Modifier.fillMaxWidth(), label = { Text(tr(label)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboardType))
}

@Composable private fun LanguagePicker(selected: AppLanguage, onSelect: (AppLanguage) -> Unit) {
    Column {
        Text(tr("Language"), style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(selected = selected == AppLanguage.ENGLISH, onClick = { onSelect(AppLanguage.ENGLISH) }, label = { Text(tr("English")) }, modifier = Modifier.testTag("languageEnglish"))
            FilterChip(selected = selected == AppLanguage.PERSIAN, onClick = { onSelect(AppLanguage.PERSIAN) }, label = { Text(tr("Persian")) }, modifier = Modifier.testTag("languagePersian"))
        }
    }
}
