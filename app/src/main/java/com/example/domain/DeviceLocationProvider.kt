package com.example.domain

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

data class DeviceLocationResult(
    val latitude: Double,
    val longitude: Double,
    val localityName: String,
    val countryName: String,
    val timezoneHours: Double,
    val isFromGps: Boolean
)

object DeviceLocationProvider {
    private const val TAG = "DeviceLocationProvider"

    fun hasLocationPermission(context: Context): Boolean {
        return try {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } catch (_: Throwable) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getDeviceLocation(context: Context): DeviceLocationResult? = withContext(Dispatchers.IO) {
        try {
            if (!hasLocationPermission(context)) {
                Log.d(TAG, "Location permission not granted.")
                return@withContext null
            }
        } catch (_: Throwable) {
            return@withContext null
        }

        var loc: Location? = null

        // 1. Try Google Play Services FusedLocationProviderClient first
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            loc = suspendCancellableCoroutine { continuation ->
                val cts = CancellationTokenSource()
                fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            if (continuation.isActive) continuation.resume(location)
                        } else {
                            // Fallback to last known location
                            fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                                if (continuation.isActive) continuation.resume(lastLoc)
                            }.addOnFailureListener {
                                if (continuation.isActive) continuation.resume(null)
                            }
                        }
                    }
                    .addOnFailureListener {
                        fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                            if (continuation.isActive) continuation.resume(lastLoc)
                        }.addOnFailureListener {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                continuation.invokeOnCancellation {
                    try { cts.cancel() } catch (_: Throwable) {}
                }
            }
        } catch (e: Throwable) {
            Log.w(TAG, "FusedLocationProvider exception: ${e.message}")
        }

        // 2. Fallback to Android Framework LocationManager if Fused returns null
        if (loc == null) {
            try {
                val locMgr = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                if (locMgr != null) {
                    val gpsLoc = try { locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: Throwable) { null }
                    val netLoc = try { locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: Throwable) { null }
                    val passiveLoc = try { locMgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) } catch (_: Throwable) { null }

                    loc = listOfNotNull(gpsLoc, netLoc, passiveLoc).maxByOrNull { it.time }
                }
            } catch (e: Throwable) {
                Log.w(TAG, "LocationManager fallback error: ${e.message}")
            }
        }

        if (loc == null) {
            Log.w(TAG, "Unable to resolve device location.")
            return@withContext null
        }

        // 3. Reverse Geocode Coordinates to City & Country Name
        var locality = ""
        var country = ""

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = suspendCancellableCoroutine<List<android.location.Address>?> { cont ->
                    geocoder.getFromLocation(loc.latitude, loc.longitude, 1) { addrs ->
                        cont.resume(addrs)
                    }
                }
                val addr = addresses?.firstOrNull()
                if (addr != null) {
                    locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: ""
                    country = addr.countryName ?: ""
                }
            } else {
                @Suppress("DEPRECATION")
                val addrs = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                val addr = addrs?.firstOrNull()
                if (addr != null) {
                    locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: ""
                    country = addr.countryName ?: ""
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Reverse geocoding error: ${e.message}")
        }

        val formattedName = when {
            locality.isNotBlank() && country.isNotBlank() -> "$locality, $country"
            locality.isNotBlank() -> locality
            country.isNotBlank() -> country
            else -> {
                val latStr = "%.2f°%s".format(kotlin.math.abs(loc.latitude), if (loc.latitude >= 0) "N" else "S")
                val lngStr = "%.2f°%s".format(kotlin.math.abs(loc.longitude), if (loc.longitude >= 0) "E" else "W")
                "$latStr, $lngStr"
            }
        }

        val tzOffsetHours = (TimeZone.getDefault().rawOffset / 3600000.0)

        DeviceLocationResult(
            latitude = loc.latitude,
            longitude = loc.longitude,
            localityName = formattedName,
            countryName = country.ifBlank { "GPS Coordinates" },
            timezoneHours = tzOffsetHours,
            isFromGps = true
        )
    }
}
