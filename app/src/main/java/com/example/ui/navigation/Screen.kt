package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Discovery : Screen("discovery")
    object TempleDetail : Screen("temple_detail/{templeId}") {
        fun createRoute(templeId: String) = "temple_detail/$templeId"
    }
    object SevakDashboard : Screen("sevak_dashboard")
    object LiveCall : Screen("live_call/{channelId}/{templeName}/{sevakName}/{devoteeName}/{isSevak}?sankalpa={sankalpa}") {
        fun createRoute(
            channelId: String,
            templeName: String,
            sevakName: String,
            devoteeName: String,
            isSevak: Boolean,
            sankalpa: String = "Peace and Blessings"
        ): String {
            val encodedTemple = java.net.URLEncoder.encode(templeName, "UTF-8")
            val encodedSevak = java.net.URLEncoder.encode(sevakName, "UTF-8")
            val encodedDevotee = java.net.URLEncoder.encode(devoteeName, "UTF-8")
            val encodedSankalpa = java.net.URLEncoder.encode(sankalpa, "UTF-8")
            return "live_call/$channelId/$encodedTemple/$encodedSevak/$encodedDevotee/$isSevak?sankalpa=$encodedSankalpa"
        }
    }
    object OfferingsHistory : Screen("offerings_history")
    object SevakProfile : Screen("sevak_profile/{sevakId}") {
        fun createRoute(sevakId: String) = "sevak_profile/$sevakId"
    }
}
