package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.UserRole
import com.example.ui.components.IncomingRequestDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.LiveDarshanCallScreen
import com.example.ui.screens.OfferingsHistoryScreen
import com.example.ui.screens.SevakDashboardScreen
import com.example.ui.screens.TempleDetailScreen
import com.example.ui.screens.TempleDiscoveryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DarshanViewModel
import java.net.URLDecoder

class MainActivity : ComponentActivity() {

    private val viewModel: DarshanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DarshanApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DarshanApp(viewModel: DarshanViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val authUser by viewModel.authUser.collectAsStateWithLifecycle()
    val temples by viewModel.temples.collectAsStateWithLifecycle()
    val sevaks by viewModel.sevaks.collectAsStateWithLifecycle()
    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val offerings by viewModel.offerings.collectAsStateWithLifecycle()
    val geofenceState by viewModel.geofenceState.collectAsStateWithLifecycle()
    val razorpayApiKey by viewModel.razorpayService.apiKey.collectAsStateWithLifecycle()
    val incomingRequest by viewModel.activeIncomingRequest.collectAsStateWithLifecycle()
    val acceptedCall by viewModel.acceptedCallToJoin.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.messageSnackbar.collectAsStateWithLifecycle()

    // Handle snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    // Handle accepted call auto-navigation
    LaunchedEffect(acceptedCall) {
        acceptedCall?.let { req ->
            navController.navigate(
                Screen.LiveCall.createRoute(
                    channelId = req.channelId,
                    templeName = req.templeName,
                    sevakName = req.sevakName,
                    devoteeName = req.devoteeName,
                    isSevak = authUser.role == UserRole.SEVAK,
                    sankalpa = req.sankalpaPrayer
                )
            )
            viewModel.clearAcceptedCall()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Discovery.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Screen
            composable(Screen.Auth.route) {
                AuthScreen(
                    onLoginSuccess = { phone, otp, name, role ->
                        viewModel.login(phone, otp, name, role)
                        if (role == UserRole.SEVAK) {
                            navController.navigate(Screen.SevakDashboard.route)
                        } else {
                            navController.navigate(Screen.Discovery.route)
                        }
                    }
                )
            }

            // Temple Discovery Screen (Devotee Home)
            composable(Screen.Discovery.route) {
                TempleDiscoveryScreen(
                    authUser = authUser,
                    temples = temples,
                    sevaks = sevaks,
                    geofenceState = geofenceState,
                    onTempleClick = { temple ->
                        viewModel.selectTemple(temple)
                        navController.navigate(Screen.TempleDetail.createRoute(temple.id))
                    },
                    onSevakClick = { sevak, temple ->
                        viewModel.selectTemple(temple)
                        viewModel.selectSevak(sevak)
                    },
                    onSwitchRole = {
                        val newRole = if (authUser.role == UserRole.DEVOTEE) UserRole.SEVAK else UserRole.DEVOTEE
                        viewModel.switchRole(newRole)
                        if (newRole == UserRole.SEVAK) {
                            navController.navigate(Screen.SevakDashboard.route)
                        }
                    },
                    onOpenOfferings = {
                        navController.navigate(Screen.OfferingsHistory.route)
                    },
                    onDistanceSelected = { dist ->
                        viewModel.setGeofenceDistance(dist)
                    },
                    onRequestDarshanFromSheet = { temple, sevak, sankalpa, gotra, focus, offering ->
                        val req = viewModel.requestDarshan(temple, sevak, sankalpa, gotra, focus, offering)
                        // If testing in Devotee mode, prompt ready
                    }
                )
            }

            // Temple Detail Screen
            composable(
                route = Screen.TempleDetail.route,
                arguments = listOf(navArgument("templeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val templeId = backStackEntry.arguments?.getString("templeId") ?: ""
                val temple = temples.find { it.id == templeId } ?: temples.firstOrNull()

                if (temple != null) {
                    TempleDetailScreen(
                        temple = temple,
                        sevaks = sevaks,
                        geofenceState = geofenceState,
                        razorpayApiKey = razorpayApiKey,
                        onBack = { navController.popBackStack() },
                        onKeyUpdated = { viewModel.razorpayService.updateApiKey(it) },
                        onSubmitOffering = { amount, category ->
                            viewModel.makeOffering(amount, category, temple.name, "Temple Trust")
                        },
                        onRequestDarshan = { t, sevak, sankalpa, gotra, focus, offering ->
                            viewModel.requestDarshan(t, sevak, sankalpa, gotra, focus, offering)
                        }
                    )
                }
            }

            // Sevak Dashboard Screen
            composable(Screen.SevakDashboard.route) {
                SevakDashboardScreen(
                    authUser = authUser,
                    temples = temples,
                    requests = requests,
                    geofenceState = geofenceState,
                    onBack = {
                        viewModel.switchRole(UserRole.DEVOTEE)
                        navController.popBackStack()
                    },
                    onVerifyCode = { templeId, code ->
                        viewModel.verifySevakWithCode(templeId, code)
                    },
                    onUpdateBio = { bio ->
                        viewModel.updateSevakProfile(bio)
                    },
                    onDistanceSelected = { dist ->
                        viewModel.setGeofenceDistance(dist)
                    },
                    onAcceptRequest = { req ->
                        viewModel.acceptRequest(req)
                    },
                    onDeclineRequest = { req ->
                        viewModel.declineRequest(req)
                    }
                )
            }

            // Live 1-to-1 Video Darshan Screen
            composable(
                route = Screen.LiveCall.route,
                arguments = listOf(
                    navArgument("channelId") { type = NavType.StringType },
                    navArgument("templeName") { type = NavType.StringType },
                    navArgument("sevakName") { type = NavType.StringType },
                    navArgument("devoteeName") { type = NavType.StringType },
                    navArgument("isSevak") { type = NavType.BoolType },
                    navArgument("sankalpa") {
                        type = NavType.StringType
                        defaultValue = "Peace, health and blessings"
                    }
                )
            ) { backStackEntry ->
                val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
                val templeName = URLDecoder.decode(backStackEntry.arguments?.getString("templeName") ?: "", "UTF-8")
                val sevakName = URLDecoder.decode(backStackEntry.arguments?.getString("sevakName") ?: "", "UTF-8")
                val devoteeName = URLDecoder.decode(backStackEntry.arguments?.getString("devoteeName") ?: "", "UTF-8")
                val isSevak = backStackEntry.arguments?.getBoolean("isSevak") ?: false
                val sankalpa = URLDecoder.decode(backStackEntry.arguments?.getString("sankalpa") ?: "Peace and blessings", "UTF-8")

                LiveDarshanCallScreen(
                    channelId = channelId,
                    templeName = templeName,
                    sevakName = sevakName,
                    devoteeName = devoteeName,
                    sankalpa = sankalpa,
                    isSevak = isSevak,
                    webRtcEngine = viewModel.webRtcEngine,
                    razorpayApiKey = razorpayApiKey,
                    onEndCall = { navController.popBackStack() },
                    onKeyUpdated = { viewModel.razorpayService.updateApiKey(it) },
                    onSubmitOffering = { amount, category ->
                        viewModel.makeOffering(amount, category, templeName, sevakName)
                    }
                )
            }

            // Offerings & Receipts History Screen
            composable(Screen.OfferingsHistory.route) {
                OfferingsHistoryScreen(
                    offerings = offerings,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Incoming Request Dialog (Alerts Sevak or tester when a live request is received)
        if (incomingRequest != null) {
            IncomingRequestDialog(
                request = incomingRequest!!,
                isInsideGeofence = geofenceState.isInsideGeofence,
                onAccept = {
                    viewModel.acceptRequest(incomingRequest!!)
                },
                onDecline = {
                    viewModel.declineRequest(incomingRequest!!)
                }
            )
        }
    }
}
