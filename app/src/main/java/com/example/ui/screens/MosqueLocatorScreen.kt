package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.domain.CityPreset
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.google.android.gms.location.LocationServices
import kotlin.math.*

data class MosqueItem(
    val id: String,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val latitude: Double,
    val longitude: Double,
    val hasJumuah: Boolean = true,
    val hasWomensArea: Boolean = true,
    val hasParking: Boolean = true,
    val hasWuduArea: Boolean = true,
    val phone: String = "+1 (555) 345-6789"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MosqueLocatorScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") } // ALL, JUMUAH, WOMENS, NEARBY
    var isDetectingLocation by remember { mutableStateOf(false) }
    var detectedLocationName by remember { mutableStateOf<String?>(null) }
    var userGpsLocation by remember { mutableStateOf<Location?>(null) }

    val activeLat = userGpsLocation?.latitude ?: state.selectedCity.latitude
    val activeLng = userGpsLocation?.longitude ?: state.selectedCity.longitude
    val locationLabel = detectedLocationName ?: state.selectedCity.name

    val allMosques = remember(activeLat, activeLng, locationLabel) {
        generateNearbyMosques(locationLabel, activeLat, activeLng)
    }

    val filteredMosques = remember(allMosques, searchQuery, filterType) {
        allMosques.filter { mosque ->
            val matchesQuery = searchQuery.isBlank() ||
                    mosque.name.contains(searchQuery, ignoreCase = true) ||
                    mosque.address.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (filterType) {
                "JUMUAH" -> mosque.hasJumuah
                "WOMENS" -> mosque.hasWomensArea
                "NEARBY" -> mosque.distanceKm <= 2.0
                else -> true
            }

            matchesQuery && matchesFilter
        }.sortedBy { it.distanceKm }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isDetectingLocation = true
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    isDetectingLocation = false
                    if (loc != null) {
                        userGpsLocation = loc
                        detectedLocationName = "Current GPS Location"
                        viewModel.updateCoordinates(loc.latitude, loc.longitude, "My Location")
                        Toast.makeText(context, "Location updated from GPS!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unable to read GPS location. Using ${state.selectedCity.name}.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    isDetectingLocation = false
                    Toast.makeText(context, "Location error. Using ${state.selectedCity.name}.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                isDetectingLocation = false
            }
        } else {
            Toast.makeText(context, "Location permission is needed for live GPS search.", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestLocationDetection() {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            isDetectingLocation = true
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    isDetectingLocation = false
                    if (loc != null) {
                        userGpsLocation = loc
                        detectedLocationName = "Current GPS Location"
                        viewModel.updateCoordinates(loc.latitude, loc.longitude, "My Location")
                        Toast.makeText(context, "Location updated from GPS!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Using ${state.selectedCity.name} coordinates.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    isDetectingLocation = false
                }
            } catch (e: SecurityException) {
                isDetectingLocation = false
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header & Location detection card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().testTag("mosque_locator_header")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Mosque, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                            }
                        }
                        Column {
                            Text(
                                text = "Mosque Locator",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Nearest masajid in $locationLabel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // GPS Detect Button
                    FilledTonalButton(
                        onClick = { requestLocationDetection() },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        enabled = !isDetectingLocation,
                        modifier = Modifier.testTag("detect_gps_location_btn")
                    ) {
                        if (isDetectingLocation) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("GPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search mosque name or address...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("mosque_search_input"),
                    singleLine = true
                )

                // Filter Chips & Open Full Map Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        FilterChip(
                            selected = filterType == "ALL",
                            onClick = { filterType = "ALL" },
                            label = { Text("All (${allMosques.size})", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = filterType == "NEARBY",
                            onClick = { filterType = "NEARBY" },
                            label = { Text("< 2km", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = filterType == "WOMENS",
                            onClick = { filterType = "WOMENS" },
                            label = { Text("Women's", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = filterType == "JUMUAH",
                            onClick = { filterType = "JUMUAH" },
                            label = { Text("Jumu'ah", fontSize = 11.sp) }
                        )
                    }

                    IconButton(
                        onClick = {
                            val uri = Uri.parse("geo:$activeLat,$activeLng?q=mosque")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                val web = Uri.parse("https://www.google.com/maps/search/mosques/@$activeLat,$activeLng,14z")
                                context.startActivity(Intent(Intent.ACTION_VIEW, web))
                            }
                        },
                        modifier = Modifier.testTag("open_maps_search_btn")
                    ) {
                        Icon(Icons.Default.Map, contentDescription = "Google Maps Search", tint = EmeraldPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PRO Banner if not subscribed
        if (!state.coinsState.isPremium) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GoldAccent.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable { viewModel.openCoinsHub() }
                    .testTag("mosque_locator_pro_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mosque Locator is a PRO Feature",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Unlock turn-by-turn navigation with 24 Deen Coins (earned via 5 Namaz or Fasting today).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { viewModel.openCoinsHub() },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("unlock_mosque_pro_btn")
                    ) {
                        Text("Unlock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Mosques Grid (Responsive 2 columns on tablets/large phones)
        if (filteredMosques.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Mosque, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                    Text(
                        text = "No mosques match your criteria",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { searchQuery = ""; filterType = "ALL" }) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                modifier = Modifier.fillMaxSize().testTag("mosques_grid"),
                contentPadding = PaddingValues(bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMosques, key = { it.id }) { mosque ->
                    MosqueCard(
                        mosque = mosque,
                        isPremium = state.coinsState.isPremium,
                        onNavigate = {
                            if (!state.coinsState.isPremium) {
                                viewModel.openCoinsHub()
                                Toast.makeText(context, "Unlock PRO with Deen Coins for direct Google Maps directions!", Toast.LENGTH_SHORT).show()
                            } else {
                                launchGoogleMapsNavigation(context, mosque)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MosqueCard(
    mosque: MosqueItem,
    isPremium: Boolean = true,
    onNavigate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("mosque_card_${mosque.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mosque.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = mosque.address,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        Text(
                            text = "${mosque.distanceKm} km",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                    }
                }
            }

            // Facilities tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (mosque.hasJumuah) {
                    FacilityBadge("Jumu'ah", Icons.Default.Groups)
                }
                if (mosque.hasWomensArea) {
                    FacilityBadge("Women's Area", Icons.Default.Female)
                }
                if (mosque.hasParking) {
                    FacilityBadge("Parking", Icons.Default.LocalParking)
                }
                if (mosque.hasWuduArea) {
                    FacilityBadge("Wudu", Icons.Default.WaterDrop)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Navigation Button
            Button(
                onClick = onNavigate,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPremium) EmeraldPrimary else GoldAccent,
                    contentColor = if (isPremium) Color.White else Color.Black
                ),
                modifier = Modifier.fillMaxWidth().testTag("navigate_button_${mosque.id}")
            ) {
                Icon(
                    imageVector = if (isPremium) Icons.Default.Navigation else Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (isPremium) "Navigate in Google Maps" else "Unlock Navigation (PRO)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun FacilityBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun launchGoogleMapsNavigation(context: Context, mosque: MosqueItem) {
    val uri = Uri.parse("google.navigation:q=${mosque.latitude},${mosque.longitude}&mode=d")
    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        // Fallback to browser or generic map viewer
        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${mosque.latitude},${mosque.longitude}&destination_place_id=${Uri.encode(mosque.name)}")
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        try {
            context.startActivity(webIntent)
        } catch (err: Exception) {
            Toast.makeText(context, "Could not open map application.", Toast.LENGTH_SHORT).show()
        }
    }
}

fun generateNearbyMosques(cityName: String, baseLat: Double, baseLng: Double): List<MosqueItem> {
    return listOf(
        MosqueItem(
            id = "mosque_1",
            name = "Masjid Al-Farooq & Islamic Center",
            address = "142 Grand Avenue, $cityName Center",
            distanceKm = 0.7,
            latitude = baseLat + 0.0051,
            longitude = baseLng + 0.0042,
            hasJumuah = true,
            hasWomensArea = true,
            hasParking = true,
            hasWuduArea = true
        ),
        MosqueItem(
            id = "mosque_2",
            name = "Jamia Masjid An-Noor",
            address = "88 Crescent Boulevard, $cityName",
            distanceKm = 1.3,
            latitude = baseLat - 0.0083,
            longitude = baseLng + 0.0064,
            hasJumuah = true,
            hasWomensArea = true,
            hasParking = true,
            hasWuduArea = true
        ),
        MosqueItem(
            id = "mosque_3",
            name = "Baitul Mukarram Community Masjid",
            address = "215 South Garden Way, $cityName",
            distanceKm = 1.9,
            latitude = baseLat + 0.0121,
            longitude = baseLng - 0.0094,
            hasJumuah = true,
            hasWomensArea = true,
            hasParking = true,
            hasWuduArea = true
        ),
        MosqueItem(
            id = "mosque_4",
            name = "Masjid Bilal (Downtown Musalla)",
            address = "12 Market Plaza, 3rd Floor, $cityName",
            distanceKm = 2.4,
            latitude = baseLat - 0.0152,
            longitude = baseLng - 0.0116,
            hasJumuah = true,
            hasWomensArea = false,
            hasParking = false,
            hasWuduArea = true
        ),
        MosqueItem(
            id = "mosque_5",
            name = "Islamic Cultural Society & Masjid",
            address = "500 University Park Road, $cityName",
            distanceKm = 3.6,
            latitude = baseLat + 0.0254,
            longitude = baseLng + 0.0182,
            hasJumuah = true,
            hasWomensArea = true,
            hasParking = true,
            hasWuduArea = true
        ),
        MosqueItem(
            id = "mosque_6",
            name = "Masjid At-Taqwa",
            address = "77 Peace Avenue, West $cityName",
            distanceKm = 4.2,
            latitude = baseLat - 0.0281,
            longitude = baseLng + 0.0227,
            hasJumuah = true,
            hasWomensArea = true,
            hasParking = true,
            hasWuduArea = true
        )
    )
}
