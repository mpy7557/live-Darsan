package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AuthUser
import com.example.data.model.DarshanRequest
import com.example.data.model.Temple
import com.example.service.GeofenceState
import com.example.ui.components.GeofenceBanner
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevakDashboardScreen(
    authUser: AuthUser,
    temples: List<Temple>,
    requests: List<DarshanRequest>,
    geofenceState: GeofenceState,
    onBack: () -> Unit,
    onVerifyCode: (templeId: String, code: String) -> Boolean,
    onUpdateBio: (String) -> Unit,
    onDistanceSelected: (Float) -> Unit,
    onAcceptRequest: (DarshanRequest) -> Unit,
    onDeclineRequest: (DarshanRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var enteredCode by remember { mutableStateOf("") }
    var selectedTempleId by remember { mutableStateOf(temples.firstOrNull()?.id ?: "temple_kashi") }
    var isTempleDropdownExpanded by remember { mutableStateOf(false) }

    var bioText by remember { mutableStateOf(authUser.sevakBio) }
    var isEditingBio by remember { mutableStateOf(false) }
    var isOnlineBroadcast by remember { mutableStateOf(true) }

    val pendingRequests = requests.filter { it.status == "PENDING" }
    val selectedTemple = temples.find { it.id == selectedTempleId } ?: temples.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Temple Sevak Portal",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("sevak_dashboard_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Geofence Banner
            item {
                GeofenceBanner(
                    geofenceState = geofenceState,
                    onDistanceSelected = onDistanceSelected
                )
            }

            // 1. Verification Section (if not yet verified)
            if (!authUser.isSevakVerified) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SaffronPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = "Temple Verification Code",
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Temple Authorization Required",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Enter temple-issued code to become a verified darshan provider",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Temple selector dropdown
                            ExposedDropdownMenuBox(
                                expanded = isTempleDropdownExpanded,
                                onExpandedChange = { isTempleDropdownExpanded = !isTempleDropdownExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedTemple?.name ?: "Select Temple",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Permitted Temple") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTempleDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = isTempleDropdownExpanded,
                                    onDismissRequest = { isTempleDropdownExpanded = false }
                                ) {
                                    temples.forEach { t ->
                                        DropdownMenuItem(
                                            text = { Text("${t.name} (${t.city})") },
                                            onClick = {
                                                selectedTempleId = t.id
                                                isTempleDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = enteredCode,
                                onValueChange = { enteredCode = it },
                                label = { Text("Temple Authorization Code") },
                                placeholder = { Text("e.g. ${selectedTemple?.verifiedAccessCode ?: "KASHI108"}") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SaffronPrimary)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("temple_verification_code_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Hint for preview testers
                            Text(
                                text = "Preview Sample Codes: KASHI108 (Kashi), TIRU777 (Tirupati), SIDDHI21 (Siddhivinayak), SOM999 (Somnath), or SEVA108",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = SaffronPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onVerifyCode(selectedTempleId, enteredCode)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_temple_code_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Verify & Activate Provider Badge", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // 2. Verified Sevak Profile & Presence Card
                item {
                    val verifiedTemple = temples.find { it.id == authUser.verifiedTempleId } ?: temples.first()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, SaffronPrimary, CircleShape)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_sevak_portrait_1790748141779),
                                            contentDescription = "Sevak Avatar",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = authUser.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified",
                                                tint = VerifiedGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Text(
                                            text = "Verified Sevak at ${verifiedTemple.name}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = SaffronPrimary
                                        )
                                    }
                                }

                                // Online Switch
                                Column(horizontalAlignment = Alignment.End) {
                                    Switch(
                                        checked = isOnlineBroadcast && geofenceState.isInsideGeofence,
                                        onCheckedChange = { isOnlineBroadcast = it },
                                        enabled = geofenceState.isInsideGeofence,
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = VerifiedGreen
                                        ),
                                        modifier = Modifier.testTag("sevak_online_switch")
                                    )
                                    Text(
                                        text = if (isOnlineBroadcast && geofenceState.isInsideGeofence) "Online" else "Offline",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOnlineBroadcast && geofenceState.isInsideGeofence) VerifiedGreen else Color.Gray
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Sevak Bio (shown before devotees request)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your Spiritual Bio (Shown to Devotees):",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEditingBio) "Done" else "Edit",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            if (isEditingBio) {
                                                onUpdateBio(bioText)
                                                isEditingBio = false
                                            } else {
                                                isEditingBio = true
                                            }
                                        }
                                        .padding(4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (isEditingBio) {
                                OutlinedTextField(
                                    value = bioText,
                                    onValueChange = { bioText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sevak_bio_editor_input"),
                                    maxLines = 3
                                )
                            } else {
                                Text(
                                    text = authUser.sevakBio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 3. Incoming Devotee Darshan Requests Queue
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pending Darshan Requests",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${pendingRequests.size} Waiting",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    )
                }
            }

            if (pendingRequests.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No active requests waiting right now.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Devotees browsing ${selectedTemple?.name ?: "the temple"} can see you online and send requests.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(pendingRequests) { req ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pending_request_item_${req.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SaffronPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = req.devoteeName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                if (req.offeringAmount > 0) {
                                    Text(
                                        text = "₹${req.offeringAmount} Dakshina",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Sanctum Focus: ${req.focusPreference}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = SaffronPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Sankalpa: \"${req.sankalpaPrayer}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (req.familyGotra.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Gotra / Names: ${req.familyGotra}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onDeclineRequest(req) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Decline")
                                }

                                Button(
                                    onClick = { onAcceptRequest(req) },
                                    enabled = geofenceState.isInsideGeofence,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Accept Call")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
