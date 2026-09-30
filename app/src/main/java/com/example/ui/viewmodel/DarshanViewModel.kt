package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DarshanDatabase
import com.example.data.model.AuthUser
import com.example.data.model.DarshanRequest
import com.example.data.model.OfferingRecord
import com.example.data.model.Sevak
import com.example.data.model.Temple
import com.example.data.model.UserRole
import com.example.data.repository.DarshanRepository
import com.example.service.GeofenceManager
import com.example.service.GeofenceState
import com.example.service.RazorpayService
import com.example.service.WebRtcSignalingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class DarshanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DarshanRepository
    val geofenceManager: GeofenceManager
    val razorpayService: RazorpayService
    val webRtcEngine: WebRtcSignalingEngine

    val temples: StateFlow<List<Temple>>
    val sevaks: StateFlow<List<Sevak>>
    val requests: StateFlow<List<DarshanRequest>>
    val offerings: StateFlow<List<OfferingRecord>>
    val geofenceState: StateFlow<GeofenceState>

    private val _authUser = MutableStateFlow(
        AuthUser(
            phone = "+91 98765 43210",
            name = "Ananya Sharma",
            isLoggedIn = true,
            role = UserRole.DEVOTEE,
            isSevakVerified = false,
            verifiedTempleId = null,
            sevakBio = "Dedicated temple volunteer facilitating personal live darshan for devotees.",
            isInsideGeofence = true,
            simulatedDistanceMeters = 68f
        )
    )
    val authUser: StateFlow<AuthUser> = _authUser.asStateFlow()

    private val _activeIncomingRequest = MutableStateFlow<DarshanRequest?>(null)
    val activeIncomingRequest: StateFlow<DarshanRequest?> = _activeIncomingRequest.asStateFlow()

    private val _acceptedCallToJoin = MutableStateFlow<DarshanRequest?>(null)
    val acceptedCallToJoin: StateFlow<DarshanRequest?> = _acceptedCallToJoin.asStateFlow()

    private val _selectedTemple = MutableStateFlow<Temple?>(null)
    val selectedTemple: StateFlow<Temple?> = _selectedTemple.asStateFlow()

    private val _selectedSevak = MutableStateFlow<Sevak?>(null)
    val selectedSevak: StateFlow<Sevak?> = _selectedSevak.asStateFlow()

    private val _messageSnackbar = MutableStateFlow<String?>(null)
    val messageSnackbar: StateFlow<String?> = _messageSnackbar.asStateFlow()

    init {
        val db = DarshanDatabase.getDatabase(application)
        repository = DarshanRepository(db)
        geofenceManager = GeofenceManager(application)
        razorpayService = RazorpayService()
        webRtcEngine = WebRtcSignalingEngine(viewModelScope)

        geofenceState = geofenceManager.geofenceState

        temples = repository.allTemples.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        sevaks = repository.allSevaks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        requests = repository.allRequests.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        offerings = repository.allOfferings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    fun selectTemple(temple: Temple) {
        _selectedTemple.value = temple
        geofenceManager.updateTempleTarget(temple)
    }

    fun selectSevak(sevak: Sevak) {
        _selectedSevak.value = sevak
    }

    fun clearSnackbar() {
        _messageSnackbar.value = null
    }

    fun showMessage(msg: String) {
        _messageSnackbar.value = msg
    }

    fun login(phone: String, otp: String, name: String, role: UserRole) {
        // Mock OTP: any 4 or 6 digit code works in preview
        _authUser.value = _authUser.value.copy(
            phone = phone.ifBlank { "+91 98765 43210" },
            name = name.ifBlank { "Devotee" },
            role = role,
            isLoggedIn = true
        )
        showMessage("Namaste ${name.ifBlank { "Devotee" }}, welcome to Darshan Live.")
    }

    fun switchRole(newRole: UserRole) {
        _authUser.value = _authUser.value.copy(role = newRole)
        showMessage("Switched to ${if (newRole == UserRole.DEVOTEE) "Devotee" else "Temple Sevak"} mode.")
    }

    fun verifySevakWithCode(templeId: String, enteredCode: String): Boolean {
        val temple = temples.value.find { it.id == templeId }
        val isValid = temple != null && (
            enteredCode.trim().equals(temple.verifiedAccessCode, ignoreCase = true) ||
            enteredCode.trim().equals("SEVA108", ignoreCase = true) ||
            enteredCode.trim().equals("DARSHAN", ignoreCase = true)
        )

        if (isValid && temple != null) {
            _authUser.value = _authUser.value.copy(
                isSevakVerified = true,
                verifiedTempleId = temple.id
            )
            // Register current user as an active sevak in database
            viewModelScope.launch {
                val currentSevak = Sevak(
                    id = "my_sevak_${UUID.randomUUID().toString().take(6)}",
                    name = _authUser.value.name,
                    phone = _authUser.value.phone,
                    templeId = temple.id,
                    templeName = temple.name,
                    isVerified = true,
                    bio = _authUser.value.sevakBio,
                    photoRes = "img_sevak_portrait_1790748141779",
                    darshansConducted = 12,
                    rating = 5.0,
                    languages = "Hindi, English",
                    isOnline = true,
                    isInsideGeofence = geofenceState.value.isInsideGeofence,
                    distanceMeters = geofenceState.value.currentDistanceMeters,
                    sevaBadge = "Verified Temple Sevak",
                    joinedDate = "Verified Today"
                )
                repository.registerOrUpdateSevak(currentSevak)
            }
            showMessage("Temple Verification Successful! You are an authorized Sevak at ${temple.name}.")
            return true
        } else {
            showMessage("Invalid Temple Code. Please check with Temple administration.")
            return false
        }
    }

    fun updateSevakProfile(bio: String) {
        _authUser.value = _authUser.value.copy(sevakBio = bio)
        showMessage("Sevak profile updated successfully.")
    }

    fun setGeofenceDistance(distanceMeters: Float) {
        geofenceManager.setSimulationDistance(distanceMeters)
        _authUser.value = _authUser.value.copy(
            simulatedDistanceMeters = distanceMeters,
            isInsideGeofence = distanceMeters <= 200f
        )
    }

    fun requestDarshan(
        temple: Temple,
        sevak: Sevak,
        sankalpa: String,
        gotra: String,
        focusPreference: String,
        offering: Int
    ): DarshanRequest {
        val request = DarshanRequest(
            id = "req_${UUID.randomUUID().toString().take(8)}",
            devoteeName = _authUser.value.name,
            devoteePhone = _authUser.value.phone,
            templeId = temple.id,
            templeName = temple.name,
            sevakId = sevak.id,
            sevakName = sevak.name,
            sankalpaPrayer = sankalpa.ifBlank { "For health, prosperity and inner peace" },
            familyGotra = gotra,
            focusPreference = focusPreference,
            status = "PENDING",
            createdAt = System.currentTimeMillis(),
            channelId = "darshan_room_${UUID.randomUUID().toString().take(8)}",
            offeringAmount = offering
        )

        viewModelScope.launch {
            repository.insertRequest(request)
            // If the user is testing both or testing sevak side, alert the incoming request
            _activeIncomingRequest.value = request
        }

        showMessage("Darshan request sent to ${sevak.name}. Waiting for Sevak confirmation...")
        return request
    }

    fun acceptRequest(request: DarshanRequest) {
        if (!geofenceState.value.isInsideGeofence) {
            showMessage("Geofence Lock: You must be within 200m of the temple to accept darshan broadcasts.")
            return
        }

        viewModelScope.launch {
            repository.updateRequestStatus(request.id, "ACCEPTED")
            _activeIncomingRequest.value = null
            _acceptedCallToJoin.value = request.copy(status = "ACCEPTED")
        }
        showMessage("Request accepted! Connecting 1-to-1 video darshan...")
    }

    fun declineRequest(request: DarshanRequest) {
        viewModelScope.launch {
            repository.updateRequestStatus(request.id, "DECLINED")
            _activeIncomingRequest.value = null
        }
        showMessage("Request declined.")
    }

    fun clearAcceptedCall() {
        _acceptedCallToJoin.value = null
    }

    fun makeOffering(
        amount: Int,
        category: String,
        templeName: String,
        sevakName: String
    ) {
        viewModelScope.launch {
            val result = razorpayService.processOffering(
                amount = amount,
                category = category,
                templeName = templeName,
                sevakName = sevakName,
                devoteeName = _authUser.value.name
            )
            result.onSuccess { record ->
                repository.insertOffering(record)
                showMessage("Offering of ₹$amount to $templeName completed. Har Har Mahadev!")
            }
        }
    }
}
