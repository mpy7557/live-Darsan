package com.example.data.repository

import com.example.data.local.DarshanDatabase
import com.example.data.model.DarshanRequest
import com.example.data.model.OfferingRecord
import com.example.data.model.Sevak
import com.example.data.model.Temple
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DarshanRepository(private val db: DarshanDatabase) {

    val allTemples: Flow<List<Temple>> = db.templeDao().getAllTemples()
    val allSevaks: Flow<List<Sevak>> = db.sevakDao().getAllSevaks()
    val allRequests: Flow<List<DarshanRequest>> = db.darshanRequestDao().getAllRequests()
    val allOfferings: Flow<List<OfferingRecord>> = db.offeringDao().getAllOfferings()

    fun getTempleById(id: String): Flow<Temple?> = db.templeDao().getTempleById(id)
    fun getSevaksForTemple(templeId: String): Flow<List<Sevak>> = db.sevakDao().getSevaksForTemple(templeId)
    fun getSevakById(id: String): Flow<Sevak?> = db.sevakDao().getSevakById(id)
    fun getRequestsForSevak(sevakId: String): Flow<List<DarshanRequest>> = db.darshanRequestDao().getRequestsForSevak(sevakId)
    fun getRequestById(id: String): Flow<DarshanRequest?> = db.darshanRequestDao().getRequestById(id)

    suspend fun insertRequest(request: DarshanRequest) {
        db.darshanRequestDao().insertRequest(request)
    }

    suspend fun updateRequestStatus(id: String, status: String) {
        db.darshanRequestDao().updateRequestStatus(id, status)
    }

    suspend fun deleteRequest(id: String) {
        db.darshanRequestDao().deleteRequest(id)
    }

    suspend fun insertOffering(offering: OfferingRecord) {
        db.offeringDao().insertOffering(offering)
    }

    suspend fun updateSevakPresence(id: String, isOnline: Boolean, isInside: Boolean, dist: Float) {
        db.sevakDao().updateSevakPresence(id, isOnline, isInside, dist)
    }

    suspend fun updateSevakVerification(id: String, isVerified: Boolean) {
        db.sevakDao().updateVerification(id, isVerified)
    }

    suspend fun registerOrUpdateSevak(sevak: Sevak) {
        db.sevakDao().insertSevak(sevak)
    }

    suspend fun initializeSeedDataIfEmpty() {
        val existingTemples = allTemples.first()
        if (existingTemples.isEmpty()) {
            val seedTemples = listOf(
                Temple(
                    id = "temple_kashi",
                    name = "Kashi Vishwanath Temple",
                    deity = "Lord Shiva (Vishwanatha)",
                    city = "Varanasi",
                    state = "Uttar Pradesh",
                    latitude = 25.3109,
                    longitude = 83.0107,
                    verifiedAccessCode = "KASHI108",
                    description = "One of the most sacred 12 Jyotirlingas, situated on the western bank of holy Ganga. Permitted for 1-to-1 sanctum parikrama and Aarti darshan.",
                    timings = "04:00 AM - 11:00 PM",
                    activeSevaksCount = 2,
                    sanctumFocusAreas = "Garbhagriha Shivalinga, Mangala Aarti, Nandi Shrine, Ganga View Corridor"
                ),
                Temple(
                    id = "temple_tirupati",
                    name = "Tirupati Balaji Temple",
                    deity = "Lord Venkateswara",
                    city = "Tirumala",
                    state = "Andhra Pradesh",
                    latitude = 13.6833,
                    longitude = 79.3472,
                    verifiedAccessCode = "TIRU777",
                    description = "Sacred shrine atop the seven hills of Seshachalam. Permitted sevaks facilitate one-on-one live darshan from the sanctum approach and Dwajasthambham.",
                    timings = "03:30 AM - 11:30 PM",
                    activeSevaksCount = 2,
                    sanctumFocusAreas = "Ananda Nilayam Vimana, Dwajasthambham, Garudazhwan Shrine, Hundi Seva"
                ),
                Temple(
                    id = "temple_siddhivinayak",
                    name = "Siddhivinayak Temple",
                    deity = "Shri Ganesha",
                    city = "Prabhadevi, Mumbai",
                    state = "Maharashtra",
                    latitude = 19.0169,
                    longitude = 72.8303,
                    verifiedAccessCode = "SIDDHI21",
                    description = "Famed wish-fulfilling Ganesha temple. Authorized volunteers provide live video darshan of gold sanctum & morning Kakad Aarti.",
                    timings = "05:30 AM - 10:00 PM",
                    activeSevaksCount = 1,
                    sanctumFocusAreas = "Gold Plated Gabhara, Kakad Aarti, Modak Archana, Silver Chhatra"
                ),
                Temple(
                    id = "temple_somnath",
                    name = "Somnath Jyotirlinga Temple",
                    deity = "Lord Shiva (Somnath)",
                    city = "Prabhas Patan",
                    state = "Gujarat",
                    latitude = 20.8880,
                    longitude = 70.4013,
                    verifiedAccessCode = "SOM999",
                    description = "The first of the twelve sacred Jyotirlingas along the Arabian Sea. Live evening deepa darshan and sound of holy ocean waves.",
                    timings = "06:00 AM - 09:30 PM",
                    activeSevaksCount = 1,
                    sanctumFocusAreas = "Sanctum Jyotirlinga, Digvijay Dwar, Baan Stambh, Sagar Darshan"
                ),
                Temple(
                    id = "temple_mahakal",
                    name = "Mahakaleshwar Jyotirlinga",
                    deity = "Lord Shiva (Mahakal)",
                    city = "Ujjain",
                    state = "Madhya Pradesh",
                    latitude = 23.1827,
                    longitude = 75.7682,
                    verifiedAccessCode = "MAHA555",
                    description = "Dakshinmukhi (south-facing) Jyotirlinga world-renowned for the sacred dawn Bhasma Aarti. Verified sevaks stream holy parikrama.",
                    timings = "04:00 AM - 11:00 PM",
                    activeSevaksCount = 1,
                    sanctumFocusAreas = "Bhasma Aarti View, Kotiteerth Kunda, Nagchandreshwar, Omkareshwar Shrine"
                ),
                Temple(
                    id = "temple_meenakshi",
                    name = "Meenakshi Amman Temple",
                    deity = "Goddess Meenakshi & Sundareswarar",
                    city = "Madurai",
                    state = "Tamil Nadu",
                    latitude = 9.9195,
                    longitude = 78.1193,
                    verifiedAccessCode = "MEENA108",
                    description = "Historic architectural marvel with soaring gopurams. Verified sevaks provide live video darshan through sacred pillar halls.",
                    timings = "05:00 AM - 10:00 PM",
                    activeSevaksCount = 1,
                    sanctumFocusAreas = "Golden Lotus Tank (Potramarai Kulam), Thousand Pillar Hall, Sundareswarar Sanctum"
                )
            )
            db.templeDao().insertTemples(seedTemples)

            val seedSevaks = listOf(
                Sevak(
                    id = "sevak_raghav",
                    name = "Pandit Raghav Shastri",
                    phone = "+91 94150 12345",
                    templeId = "temple_kashi",
                    templeName = "Kashi Vishwanath Temple",
                    isVerified = true,
                    bio = "Lifelong seva volunteer at Kashi Vishwanath sanctum. Facilitating live morning Bhasma Aarti and Ganga Jal parikrama for elderly devotees unable to travel.",
                    photoRes = "img_sevak_portrait_1790748141779",
                    darshansConducted = 248,
                    rating = 4.98,
                    languages = "Hindi, Sanskrit, English",
                    isOnline = true,
                    isInsideGeofence = true,
                    distanceMeters = 48f,
                    sevaBadge = "Senior Sanctum Sevak",
                    joinedDate = "Seva since Jan 2023"
                ),
                Sevak(
                    id = "sevak_sreenivas",
                    name = "Bhakti Sevak Sreenivas",
                    phone = "+91 98480 67890",
                    templeId = "temple_tirupati",
                    templeName = "Tirupati Balaji Temple",
                    isVerified = true,
                    bio = "Serving on Tirumala hills. Specializing in Sri Venkateswara suprabhatam & dwajasthambham live video darshan.",
                    photoRes = "img_sevak_portrait_1790748141779",
                    darshansConducted = 312,
                    rating = 4.95,
                    languages = "Telugu, Tamil, English, Hindi",
                    isOnline = true,
                    isInsideGeofence = true,
                    distanceMeters = 74f,
                    sevaBadge = "Tirumala Certified Sevak",
                    joinedDate = "Seva since May 2023"
                ),
                Sevak(
                    id = "sevak_mahesh",
                    name = "Mahesh Kadam",
                    phone = "+91 98200 45678",
                    templeId = "temple_siddhivinayak",
                    templeName = "Siddhivinayak Temple",
                    isVerified = true,
                    bio = "Assisting Ganesha devotees worldwide with personalized live Gabhara darshan, modak archana view & peaceful prayers.",
                    photoRes = "img_sevak_portrait_1790748141779",
                    darshansConducted = 189,
                    rating = 4.92,
                    languages = "Marathi, Hindi, English",
                    isOnline = true,
                    isInsideGeofence = true,
                    distanceMeters = 92f,
                    sevaBadge = "Gabhara Seva Volunteer",
                    joinedDate = "Seva since Aug 2023"
                ),
                Sevak(
                    id = "sevak_harshil",
                    name = "Harshil Sompura",
                    phone = "+91 97230 11223",
                    templeId = "temple_somnath",
                    templeName = "Somnath Jyotirlinga Temple",
                    isVerified = true,
                    bio = "Resident volunteer at Somnath seashore shrine. Providing serene parikrama & evening deepa darshan with sacred chants.",
                    photoRes = "img_sevak_portrait_1790748141779",
                    darshansConducted = 164,
                    rating = 4.96,
                    languages = "Gujarati, Hindi, English",
                    isOnline = true,
                    isInsideGeofence = true,
                    distanceMeters = 55f,
                    sevaBadge = "Jyotirlinga Trustee Sevak",
                    joinedDate = "Seva since Oct 2023"
                ),
                Sevak(
                    id = "sevak_alok",
                    name = "Shri Alok Trivedi",
                    phone = "+91 98930 99887",
                    templeId = "temple_mahakal",
                    templeName = "Mahakaleshwar Jyotirlinga",
                    isVerified = true,
                    bio = "Authorized volunteer in Ujjain sanctum corridor. Offering live darshan during morning & evening Shringar aarti with Rudra chant.",
                    photoRes = "img_sevak_portrait_1790748141779",
                    darshansConducted = 410,
                    rating = 4.99,
                    languages = "Hindi, Sanskrit",
                    isOnline = true,
                    isInsideGeofence = true,
                    distanceMeters = 110f,
                    sevaBadge = "Dharmik Sevak",
                    joinedDate = "Seva since Feb 2022"
                )
            )
            db.sevakDao().insertSevaks(seedSevaks)
        }
    }
}
