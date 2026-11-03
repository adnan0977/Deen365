package com.example.domain

import kotlin.math.*

data class QiblaDirection(
    val bearingDegrees: Float,
    val compassDirection: String,
    val distanceKm: Double
)

object QiblaCalculator {
    private const val KAABA_LAT = 21.422487
    private const val KAABA_LNG = 39.826206

    /**
     * Calculates the Qibla bearing in degrees (0 to 360) clockwise from True North.
     */
    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val lat1 = Math.toRadians(userLat)
        val lat2 = Math.toRadians(KAABA_LAT)
        val deltaLng = Math.toRadians(KAABA_LNG - userLng)

        val y = sin(deltaLng) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLng)

        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)
        val normalized = (bearingDeg + 360.0) % 360.0
        return normalized.toFloat()
    }

    fun calculateDistanceToKaabaKm(userLat: Double, userLng: Double): Double {
        val r = 6371.0 // Earth's radius in km
        val dLat = Math.toRadians(KAABA_LAT - userLat)
        val dLng = Math.toRadians(KAABA_LNG - userLng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(KAABA_LAT)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun getCompassDirection(bearingDeg: Float): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                                 "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = (((bearingDeg % 360) + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    fun calculateQibla(userLat: Double, userLng: Double): QiblaDirection {
        val bearing = calculateQiblaBearing(userLat, userLng)
        val distance = calculateDistanceToKaabaKm(userLat, userLng)
        val compassDir = getCompassDirection(bearing)
        return QiblaDirection(
            bearingDegrees = bearing,
            compassDirection = compassDir,
            distanceKm = distance
        )
    }
}
