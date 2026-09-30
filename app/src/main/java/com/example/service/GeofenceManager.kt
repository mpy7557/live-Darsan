package com.example.service

import android.content.Context
import android.location.Location
import com.example.data.model.Temple
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GeofenceState(
    val isInsideGeofence: Boolean = true,
    val currentDistanceMeters: Float = 68f,
    val thresholdMeters: Float = 200f,
    val isSimulationMode: Boolean = true, // enabled by default for smooth browser emulator preview
    val targetTempleName: String = "Kashi Vishwanath Temple",
    val userLatitude: Double = 25.3112,
    val userLongitude: Double = 83.0110
)

class GeofenceManager(private val context: Context) {

    private val _geofenceState = MutableStateFlow(GeofenceState())
    val geofenceState: StateFlow<GeofenceState> = _geofenceState.asStateFlow()

    fun updateTempleTarget(temple: Temple) {
        val current = _geofenceState.value
        val dist = if (current.isSimulationMode) {
            current.currentDistanceMeters
        } else {
            calculateDistance(current.userLatitude, current.userLongitude, temple.latitude, temple.longitude)
        }
        _geofenceState.value = current.copy(
            targetTempleName = temple.name,
            currentDistanceMeters = dist,
            isInsideGeofence = dist <= current.thresholdMeters
        )
    }

    fun setSimulationDistance(distanceMeters: Float) {
        val current = _geofenceState.value
        _geofenceState.value = current.copy(
            isSimulationMode = true,
            currentDistanceMeters = distanceMeters,
            isInsideGeofence = distanceMeters <= current.thresholdMeters
        )
    }

    fun setSimulationMode(enabled: Boolean) {
        _geofenceState.value = _geofenceState.value.copy(isSimulationMode = enabled)
    }

    fun updateRealLocation(lat: Double, lng: Double, temple: Temple) {
        val dist = calculateDistance(lat, lng, temple.latitude, temple.longitude)
        _geofenceState.value = _geofenceState.value.copy(
            isSimulationMode = false,
            userLatitude = lat,
            userLongitude = lng,
            targetTempleName = temple.name,
            currentDistanceMeters = dist,
            isInsideGeofence = dist <= _geofenceState.value.thresholdMeters
        )
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
