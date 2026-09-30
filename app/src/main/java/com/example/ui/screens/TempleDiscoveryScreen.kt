package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.Sevak
import com.example.data.model.Temple
import com.example.data.model.UserRole
import com.example.service.GeofenceState
import com.example.ui.components.GeofenceBanner
import com.example.ui.components.SevakProfileSheet
import com.example.ui.components.TempleCard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempleDiscoveryScreen(
    authUser: AuthUser,
    temples: List<Temple>,
    sevaks: List<Sevak>,
    geofenceState: GeofenceState,
    onTempleClick: (Temple) -> Unit,
    onSevakClick: (Sevak, Temple) -> Unit,
    onSwitchRole: () -> Unit,
    onOpenOfferings: () -> Unit,
    onDistanceSelected: (Float) -> Unit,
    onRequestDarshanFromSheet: (Temple, Sevak, String, String, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filterTabs = listOf("All", "Shiva / Jyotirlinga", "Venkateswara", "Ganesha", "Shakti")

    var sheetSevak by remember { mutableStateOf<Sevak?>(null) }
    var sheetTemple by remember { mutableStateOf<Temple?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val onlineSevaks = sevaks.filter { it.isOnline && it.isVerified }

    val filteredTemples = temples.filter { temple ->
        val matchesQuery = temple.name.contains(searchQuery, ignoreCase = true) ||
                temple.deity.contains(searchQuery, ignoreCase = true) ||
                temple.city.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Shiva / Jyotirlinga" -> temple.deity.contains("Shiva", ignoreCase = true) || temple.name.contains("Jyotirlinga", ignoreCase = true)
            "Venkateswara" -> temple.deity.contains("Venkateswara", ignoreCase = true) || temple.name.contains("Tirupati", ignoreCase = true)
            "Ganesha" -> temple.deity.contains("Ganesha", ignoreCase = true) || temple.name.contains("Siddhivinayak", ignoreCase = true)
            "Shakti" -> temple.deity.contains("Meenakshi", ignoreCase = true) || temple.deity.contains("Goddess", ignoreCase = true)
            else -> true
        }
        matchesQuery && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon_1790748115245),
                            contentDescription = "Darshan Live",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Darshan Live",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (authUser.role == UserRole.DEVOTEE) "Devotee Mode" else "Sevak Mode",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SaffronPrimary
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Role switcher pill button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onSwitchRole() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("switch_user_role_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Mode",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (authUser.role == UserRole.DEVOTEE) "Become Sevak" else "Devotee Mode",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                            )
                        }
                    }

                    // Hundi Offerings History
                    IconButton(
                        onClick = onOpenOfferings,
                        modifier = Modifier.testTag("view_offerings_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Offerings & Hundi Receipts",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
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

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search temple, deity or sacred city...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = SaffronPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_temples_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(filterTabs) { tab ->
                        FilterChip(
                            selected = selectedFilter == tab,
                            onClick = { selectedFilter = tab },
                            label = { Text(tab) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // "Online Verified Sevaks" Direct Call Shelf
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Online Verified Sevaks",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(VerifiedGreen)
                            )
                        }

                        Text(
                            text = "${onlineSevaks.size} Present",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VerifiedGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (onlineSevaks.isEmpty()) {
                        Text(
                            text = "No sevaks are currently online. Check back shortly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(onlineSevaks) { sevak ->
                                val temple = temples.find { it.id == sevak.templeId } ?: temples.firstOrNull()
                                Card(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .testTag("online_sevak_card_${sevak.id}")
                                        .clickable {
                                            if (temple != null) {
                                                sheetSevak = sevak
                                                sheetTemple = temple
                                            }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(CircleShape)
                                                    .border(1.5.dp, SaffronPrimary, CircleShape)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.img_sevak_portrait_1790748141779),
                                                    contentDescription = sevak.name,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = sevak.name.split(" ").take(2).joinToString(" "),
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                        maxLines = 1,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.Verified,
                                                        contentDescription = "Verified",
                                                        tint = VerifiedGreen,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }

                                                Text(
                                                    text = sevak.templeName,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                    color = SaffronPrimary,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Geofence presence tag
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(VerifiedGreenContainer)
                                                .padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(VerifiedGreen)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "On-site: ${sevak.distanceMeters.toInt()}m from Sanctum",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = VerifiedGreen
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = sevak.bio,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Call Directly CTA
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(SaffronPrimary)
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Videocam,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Call Directly",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Permitted Temples List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Permitted Temples for Live Darshan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${filteredTemples.size} Shrines",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(filteredTemples) { temple ->
                val templeOnlineCount = onlineSevaks.count { it.templeId == temple.id }
                TempleCard(
                    temple = temple,
                    onlineSevaksCount = templeOnlineCount,
                    onClick = { onTempleClick(temple) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Sevak Profile Sheet when tapped from discovery
    if (sheetSevak != null && sheetTemple != null) {
        SevakProfileSheet(
            sevak = sheetSevak!!,
            temple = sheetTemple!!,
            sheetState = sheetState,
            onDismiss = {
                sheetSevak = null
                sheetTemple = null
            },
            onRequestDarshan = { sankalpa, gotra, focus, offering ->
                onRequestDarshanFromSheet(sheetTemple!!, sheetSevak!!, sankalpa, gotra, focus, offering)
            }
        )
    }
}
