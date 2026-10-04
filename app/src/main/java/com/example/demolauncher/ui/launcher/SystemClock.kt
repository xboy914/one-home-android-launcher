package com.example.demolauncher.ui.launcher

import android.text.format.DateFormat
import com.example.demolauncher.data.model.AppLanguage
import com.example.demolauncher.ui.localization.LocalAppLanguage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/** Reads only the device's public clock and locale; no device identifiers or services. */
data class SystemClock(val time: String, val date: String)

@Composable
fun rememberSystemClock(): State<SystemClock> {
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val lifecycleOwner = LocalLifecycleOwner.current
    return produceState(initialValue = currentClock(context, language), context, language, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                value = currentClock(context, language)
                delay(60_000L - System.currentTimeMillis() % 60_000L)
            }
        }
    }
}

private fun currentClock(context: android.content.Context, language: AppLanguage): SystemClock {
    val now = ZonedDateTime.now()
    val locale = if (language == AppLanguage.PERSIAN) Locale.forLanguageTag("fa-IR") else Locale.ENGLISH
    val rawTime = DateFormat.getTimeFormat(context).format(Date())
    val time = if (language == AppLanguage.PERSIAN) rawTime.map { digit ->
        if (digit in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[digit - '0'] else digit
    }.joinToString("") else rawTime
    return SystemClock(
        time = time,
        date = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", locale)),
    )
}
