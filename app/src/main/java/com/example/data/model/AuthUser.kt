package com.example.data.model

enum class UserRole {
    DEVOTEE,
    SEVAK
}

data class AuthUser(
    val phone: String = "+91 98765 43210",
    val name: String = "Ananya Sharma",
    val isLoggedIn: Boolean = true,
    val role: UserRole = UserRole.DEVOTEE,
    val isSevakVerified: Boolean = false,
    val verifiedTempleId: String? = null,
    val sevakBio: String = "Dedicated volunteer for morning Aarti and parikrama live darshan seva.",
    val isInsideGeofence: Boolean = true,
    val simulatedDistanceMeters: Float = 68f
)
