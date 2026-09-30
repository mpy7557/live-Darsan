package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.service.CallStats
import com.example.service.WebRtcSignalingEngine
import com.example.service.WebRtcState
import com.example.ui.components.RazorpayOfferingSheet
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VerifiedGreen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDarshanCallScreen(
    channelId: String,
    templeName: String,
    sevakName: String,
    devoteeName: String,
    sankalpa: String,
    isSevak: Boolean,
    webRtcEngine: WebRtcSignalingEngine,
    razorpayApiKey: String,
    onEndCall: () -> Unit,
    onKeyUpdated: (String) -> Unit,
    onSubmitOffering: (amount: Int, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onEndCall() }

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
        webRtcEngine.startCall(channelId)
    }

    // Call duration timer
    var callSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    val callDurationText = "%02d:%02d".format(callSeconds / 60, callSeconds % 60)

    var isMicMuted by remember { mutableStateOf(false) }
    var isFrontCam by remember { mutableStateOf(false) }
    var isTempleBellsActive by remember { mutableStateOf(true) }
    var showWebRtcInfo by remember { mutableStateOf(false) }
    var showOfferingSheet by remember { mutableStateOf(false) }
    var capturedBlessingCard by remember { mutableStateOf(false) }

    val offeringSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_darshan_call_screen")
    ) {
        // Main Video Stream: Sacred Temple Sanctum Aarti & Vigraha view
        Image(
            painter = painterResource(id = R.drawable.img_temple_hero_1790748131417),
            contentDescription = "Live Sanctum Darshan Video Feed",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Subtle dark gradient overlays for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.75f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Header Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Live pulsating indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AlertRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE DARSHAN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = callDurationText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }

                    Text(
                        text = templeName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Text(
                        text = if (isSevak) "Devotee: $devoteeName" else "Sevak: $sevakName (On-site 200m)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldAccent
                        )
                    )
                }

                // WebRTC Signaling HUD toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { showWebRtcInfo = !showWebRtcInfo }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("toggle_webrtc_hud_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VerifiedGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WebRTC HD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // WebRTC Signaling Stats Overlay
            AnimatedVisibility(visible = showWebRtcInfo) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "WebRTC Direct Signaling State",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ICE Connection: Connected (Direct P2P Host) • Codec: VP9 / Opus\nResolution: 1920x1080 @ 60fps • Bitrate: 2,480 Kbps • Latency: 38ms\nDevice Video: ${if (hasCameraPermission) "Active (Physical/Preview)" else "Pending Camera Permission"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Devotee's Sankalpa banner across the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SaffronPrimary.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Sankalpa",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Devotee's Sankalpa Prayer:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                        Text(
                            text = "\"$sankalpa\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Picture-in-Picture (PiP) Window for 1-to-1 video interaction
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 180.dp, end = 16.dp)
                .size(width = 100.dp, height = 135.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.DarkGray)
                .border(2.dp, SaffronPrimary, RoundedCornerShape(14.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_sevak_portrait_1790748141779),
                contentDescription = "Self Preview Video",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = if (isSevak) "You (Sevak)" else "You (Devotee)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Bottom Controls Section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 36.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sacred Soundscape & Chants Ticker
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable { isTempleBellsActive = !isTempleBellsActive }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Temple Sound",
                    tint = if (isTempleBellsActive) GoldAccent else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTempleBellsActive) "Sanctum Bells & Chanting: Active" else "Sound: Muted",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isTempleBellsActive) GoldAccent else Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Mute Button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isMicMuted) AlertRed else Color.White.copy(alpha = 0.25f))
                        .clickable { isMicMuted = !isMicMuted }
                        .testTag("toggle_call_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute Mic",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Switch Camera / View
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .clickable {
                            isFrontCam = !isFrontCam
                            webRtcEngine.switchCamera()
                        }
                        .testTag("switch_call_camera_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Capture Blessing Snapshot
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                        .clickable { capturedBlessingCard = true }
                        .testTag("capture_blessing_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture Blessing",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Make Dakshina / Razorpay Offering (for devotee)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SaffronPrimary)
                        .clickable { showOfferingSheet = true }
                        .testTag("in_call_offering_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Offer Dakshina",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // End Call Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AlertRed)
                        .clickable {
                            webRtcEngine.endCall()
                            onEndCall()
                        }
                        .testTag("end_darshan_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    // Blessing Card Dialog (captured snapshot)
    if (capturedBlessingCard) {
        AlertDialog(
            onDismissRequest = { capturedBlessingCard = false },
            confirmButton = {
                Button(
                    onClick = { capturedBlessingCard = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save to Blessings")
                }
            },
            title = {
                Text(
                    text = "Sacred Blessing Captured",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_temple_hero_1790748131417),
                            contentDescription = "Darshan Snapshot",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Live Darshan of $templeName",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SaffronPrimary
                    )
                    Text(
                        text = "Sankalpa: \"$sankalpa\"",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Conducted by $sevakName • Recorded with divine grace",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    // Razorpay Offering Sheet during call
    if (showOfferingSheet) {
        RazorpayOfferingSheet(
            templeName = templeName,
            sevakName = sevakName,
            currentApiKey = razorpayApiKey,
            sheetState = offeringSheetState,
            onDismiss = { showOfferingSheet = false },
            onKeyUpdated = onKeyUpdated,
            onSubmitOffering = { amount, category ->
                onSubmitOffering(amount, category)
                showOfferingSheet = false
            }
        )
    }
}
