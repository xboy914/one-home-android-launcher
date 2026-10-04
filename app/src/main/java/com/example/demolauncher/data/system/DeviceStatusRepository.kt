package com.example.demolauncher.data.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.BatteryManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** Read-only information from Android. This repository never changes device settings. */
data class DeviceStatus(
    val batteryPercentage: Int? = null,
    val charging: Boolean = false,
    val wifiConnected: Boolean = false,
    val cellularConnected: Boolean = false,
    val cellularSignalLevel: Int? = null,
)

class DeviceStatusRepository(context: Context) {
    private val appContext = context.applicationContext

    val status: Flow<DeviceStatus> = callbackFlow {
        val connectivity = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val telephony = appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var battery: Int? = null
        var charging = false
        var signalLevel: Int? = try { telephony.signalStrength?.level } catch (_: Exception) { null }
        val networkCapabilities = ConcurrentHashMap<Network, NetworkCapabilities>()

        fun publish() {
            val capabilities = networkCapabilities.values
            trySend(DeviceStatus(
                batteryPercentage = battery,
                charging = charging,
                cellularSignalLevel = signalLevel,
                wifiConnected = capabilities.any { it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) },
                cellularConnected = capabilities.any { it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) },
            ))
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                battery = if (level >= 0 && scale > 0) (level * 100 / scale).coerceIn(0, 100) else null
                charging = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) in listOf(BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL)
                publish()
            }
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { publish() }
            override fun onLost(network: Network) { networkCapabilities.remove(network); publish() }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                networkCapabilities[network] = capabilities
                publish()
            }
        }
        appContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        connectivity.registerNetworkCallback(request, callback)
        var telephonyCallback: TelephonyCallback? = null
        var legacyListener: PhoneStateListener? = null
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                val listener = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                        signalLevel = signalStrength.level
                        publish()
                    }
                }
                telephony.registerTelephonyCallback(appContext.mainExecutor, listener)
                telephonyCallback = listener
            } else {
                @Suppress("DEPRECATION")
                val listener = object : PhoneStateListener() {
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                        signalLevel = signalStrength.level
                        publish()
                    }
                }
                @Suppress("DEPRECATION")
                telephony.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
                legacyListener = listener
            }
        } catch (_: SecurityException) { /* Wi-Fi and battery remain available without phone permission. */ }
        publish()
        awaitClose {
            connectivity.unregisterNetworkCallback(callback)
            telephonyCallback?.let { if (Build.VERSION.SDK_INT >= 31) telephony.unregisterTelephonyCallback(it) }
            legacyListener?.let { @Suppress("DEPRECATION") telephony.listen(it, PhoneStateListener.LISTEN_NONE) }
            appContext.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()
}
