package com.example.doctorlisting.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Envelope
import com.adamglin.phosphoricons.regular.InstagramLogo
import com.adamglin.phosphoricons.regular.LinkedinLogo
import com.adamglin.phosphoricons.regular.Phone
import com.adamglin.phosphoricons.regular.Star
import com.adamglin.phosphoricons.regular.Users
import com.example.data.model.Feedback
import com.example.data.repository.FeedbackRepository
import com.example.data.viewModel.DoctorViewModel
import com.example.data.viewModel.FeedbackViewModel
import com.example.doctorlisting.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import java.util.Date
import android.util.Log
import java.text.SimpleDateFormat

// Helper function to parse Google Maps URL
fun parseGoogleMapsUrl(url: String): Pair<Double, Double>? {
    val pattern = """@(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
    val matchResult = pattern.find(url)
    return matchResult?.let {
        val (lat, lng) = it.destructured
        lat.toDouble() to lng.toDouble()
    }
}

// Fixed function to correctly parse clinic position string
fun parseClinicPosition(position: String?): Pair<Double, Double>? {
    if (position == null || position.isBlank()) {
        Log.d("DoctorDetailScreen", "clinic_pos is null or blank, using default coordinates")
        return null
    }

    try {
        Log.d("DoctorDetailScreen", "Raw clinic position: '$position'")

        // Extract numbers and directions separately with regex
        val latRegex = """([\d.]+)\s*([NS])""".toRegex()
        val lngRegex = """([\d.]+)\s*([EW])""".toRegex()

        val latMatch = latRegex.find(position)
        val lngMatch = lngRegex.find(position)

        if (latMatch == null || lngMatch == null) {
            Log.e("DoctorDetailScreen", "Could not extract coordinates from: $position")
            return null
        }

        // Parse latitude
        val (latValue, latDir) = latMatch.destructured
        val latitude = latValue.toDouble()
        val finalLat = if (latDir == "S") -latitude else latitude

        // Parse longitude
        val (lngValue, lngDir) = lngMatch.destructured
        val longitude = lngValue.toDouble()
        val finalLng = if (lngDir == "W") -longitude else longitude

        Log.d("DoctorDetailScreen", "Parsed coordinates: ($finalLat, $finalLng)")
        return finalLat to finalLng
    } catch (e: Exception) {
        Log.e("DoctorDetailScreen", "Error parsing clinic position: ${e.message}")
        e.printStackTrace()
        return null
    }
}

@Composable
fun DoctorDetailScreen(
    doctorId: Int?,
    navController: NavController,
    viewModel: DoctorViewModel = viewModel(),
    feedbackViewModel: FeedbackViewModel = viewModel(
        factory = FeedbackViewModel.Factory(FeedbackRepository())
    )
) {
    val doctor by viewModel.selectedDoctor.collectAsState()
    val doctorLoading by viewModel.loading.collectAsState()
    val doctorError by viewModel.error.collectAsState()

    val feedbacks by feedbackViewModel.feedbacks.collectAsState()
    val feedbackLoading by feedbackViewModel.loading.collectAsState()
    val feedbackError by feedbackViewModel.error.collectAsState()

    LaunchedEffect(doctorId) {
        doctorId?.let { viewModel.loadDoctorDetails(it) }
        if (doctorId != null) {
            feedbackViewModel.getFeedbacksByDoctorId(doctorId)
        }
    }

    if (doctorLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (doctorError != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Error: $doctorError")
        }
        return
    }

    if (doctor == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Doctor not found")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top bar with social media icons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    val iconBackground = Color(0xFFE0F0FF)
                    val iconColor = Color(0xFF3366FF)
                    val iconSize = 12.dp
                    val circleSize = 22.dp

                    listOf(
                        PhosphorIcons.Regular.InstagramLogo,
                        PhosphorIcons.Regular.LinkedinLogo,
                        PhosphorIcons.Regular.Phone,
                        PhosphorIcons.Regular.Envelope
                    ).forEach { icon ->
                        Box(
                            modifier = Modifier
                                .size(circleSize)
                                .clip(CircleShape)
                                .background(iconBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    }
                }
            }

            // Profile Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Profile image
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.LightGray, CircleShape)
                                .padding(2.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = PhosphorIcons.Regular.Users,
                                contentDescription = "Doctor Profile",
                                modifier = Modifier.size(40.dp).align(Alignment.Center),
                                tint = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(horizontalAlignment = Alignment.Start) {
                            Text(text = doctor!!.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(text = doctor!!.specialty, fontSize = 14.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = PhosphorIcons.Regular.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${doctor!!.grade} out of 5", fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Icon(
                                        imageVector = PhosphorIcons.Regular.Users,
                                        contentDescription = "Users Icon",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Patients", fontSize = 13.sp, color = Color.Gray)
                                    Text(text = doctor!!.nbr_patients.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(text = "Payment", fontSize = 13.sp, color = Color.Gray)
                                }
                                Column {
                                    Spacer(modifier = Modifier.height(56.dp))
                                    Text(
                                        text = "${doctor!!.id * 100} DZD",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3366FF)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Education
            item {
                Text(text = "Education", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = doctor!!.specialty, fontSize = 13.sp, color = Color.DarkGray)
                Text(text = doctor!!.description, fontSize = 13.sp, color = Color.DarkGray)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Rating Section
            item {
                Column {
                    Text(text = "Rating", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "★ ${doctor!!.grade} out of 5", fontSize = 14.sp, color = Color.Gray)

                    Button(
                        onClick = { navController.navigate("doctor/${doctor!!.id}/reviews") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF3B82F6),
                            contentColor = Color.White
                        )
                    ) {
                        Text("See all")
                    }
                }
            }

            // Feedback Cards
            items(feedbacks) { feedback ->
                FeedbackCard(feedback = feedback)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Location Section
            item {
                Text(
                    text = "Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = doctor!!.clinic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = doctor!!.address,
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }

                    // Map view
                    val context = LocalContext.current
                    val defaultCoordinates = 36.7333 to 3.2833 // Default to Algiers
                    val coordinates = doctor!!.clinic_pos?.let { parseClinicPosition(it) } ?: defaultCoordinates
                    Log.d("DoctorDetailScreen", "Using coordinates: $coordinates")

                    AndroidView(
                        factory = { ctx ->
                            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))

                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setBuiltInZoomControls(true)
                                setMultiTouchControls(true)
                                minZoomLevel = 14.0  // Increased for better detail
                                maxZoomLevel = 21.0

                                // Set initial zoom level
                                controller.setZoom(15.0)  // Increased for better view of location

                                // Set center with coordinates
                                val geoPoint = GeoPoint(coordinates.first, coordinates.second)
                                Log.d("DoctorDetailScreen", "Setting map center to: $geoPoint")
                                controller.setCenter(geoPoint)

                                // Add marker
                                Marker(this).apply {
                                    position = geoPoint
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = doctor!!.clinic
                                    snippet = doctor!!.address

                                    ContextCompat.getDrawable(ctx, R.drawable.ic_location_marker)?.let { drawable ->
                                        icon = drawable
                                    }

                                    setOnMarkerClickListener { marker, _ ->
                                        marker.showInfoWindow()
                                        true
                                    }
                                }.also { overlays.add(it) }

                                // Add scale bar
                                overlays.add(ScaleBarOverlay(this).apply {
                                    setCentred(true)
                                    setScaleBarOffset(
                                        resources.displayMetrics.widthPixels / 2,
                                        resources.displayMetrics.heightPixels - 30
                                    )
                                })

                                // Force a refresh
                                invalidate()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Fixed Book Button at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = { /* Navigate to booking */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3B82F6)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Book an Appointment", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FeedbackCard(feedback: Feedback) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFFFFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Patient: ${feedback.patient_id}", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = feedback.title, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = feedback.description, fontSize = 13.sp)
            Text(
                text = "Date: ${SimpleDateFormat("yyyy-MM-dd").format(feedback.date_creation)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun vectorToBitmapDescriptor(context: Context, @DrawableRes vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}