package com.example.doctorlisting.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Envelope
import com.adamglin.phosphoricons.regular.InstagramLogo
import com.adamglin.phosphoricons.regular.LinkedinLogo
import com.adamglin.phosphoricons.regular.Phone
import com.adamglin.phosphoricons.regular.Star
import com.adamglin.phosphoricons.regular.Users
import com.example.data.model.Clinic
import com.example.data.model.Doctor
import com.example.data.model.Feedback
import com.example.data.repository.ClinicRepository
import com.example.data.repository.DoctorRepository
import com.example.data.repository.FeedbackRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import com.example.doctorlisting.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

// Helper function to parse Google Maps URL
fun parseGoogleMapsUrl(url: String): Pair<Double, Double>? {
    val pattern = """@(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
    val matchResult = pattern.find(url)
    return matchResult?.let {
        val (lat, lng) = it.destructured
        lat.toDouble() to lng.toDouble()
    }
}

@Composable
fun DoctorDetailScreen(doctorId: Int?, navController: NavController) {
    val doctorRepository = remember { DoctorRepository() }
    val clinicRepository = remember { ClinicRepository() }
    val userRepository = remember { UserRepository() }
    val feedbackRepository = remember { FeedbackRepository() }
    val doctorState = remember { mutableStateOf<Doctor?>(null) }
    val userState = remember { mutableStateOf<com.example.data.model.User?>(null) }
    val feedbacksState = remember { mutableStateOf<List<Feedback>>(emptyList()) }
    val clinicState = remember { mutableStateOf<Clinic?>(null) }

    LaunchedEffect(doctorId) {
        withContext(Dispatchers.IO) {
            val doctor = doctorId?.let { doctorRepository.getDoctorById(it) }
            doctorState.value = doctor

            doctor?.let {
                userState.value = userRepository.getUserById(it.user_id)
                feedbacksState.value = feedbackRepository.getFeedbacksByDoctorId(it.id)
                clinicState.value = clinicRepository.getClinic()
            }
        }
    }

    val doctor = doctorState.value
    val user = userState.value
    val feedbacks = feedbacksState.value
    val clinic = clinicState.value

    if (doctor == null || user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.width(30.dp))
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
                }}
            // Profile Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberImagePainter(doctor.photo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, CircleShape)
                            .padding(2.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Column ( horizontalAlignment = Alignment.Start){

                        Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = doctor.specialty, fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = PhosphorIcons.Regular.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${doctor.grade} out of 5", fontSize = 13.sp)
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
                                Text(text = doctor.nbr_patients.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Payment", fontSize = 13.sp, color = Color.Gray)
                            }
                            Column {
                                Spacer(modifier = Modifier.height(56.dp))

                                Text(text = "${doctor.id} DZD", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3366FF))
                            }
                        }
                    }

                }

                // Social Media Icons


            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment


            Spacer(modifier = Modifier.height(16.dp))

            // Education
            Text(text = "Education", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = doctor.specialty, fontSize = 13.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // Reviews Section
            Text(text = "Rating", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "★ ${doctor.grade} out of 5", fontSize = 14.sp, color = Color.Gray)

            if (feedbacks.isNotEmpty()) {
                Button(
                    onClick = { navController.navigate("doctor/${doctor.id}/reviews") },
                    modifier = Modifier.align(Alignment.End).padding(vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    )
                ) {
                    Text("See all")
                }

                feedbacks.take(2).forEach { feedback ->
                    FeedbackCard(feedback = feedback)
                }
            } else {
                Text("No reviews yet", fontSize = 14.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Section with Map
            Text(
                text = "Location",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            clinic?.let { currentClinic ->
                val context = LocalContext.current
                val (latitude, longitude) = parseGoogleMapsUrl(currentClinic.map_location) ?: run {
                    36.7050299 to 3.1739156 // Default coordinates
                }

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
                            text = currentClinic.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
//                        Text(
//                            text = currentClinic.map_location,
//                            fontSize = 13.sp,
//                            color = Color.DarkGray
//                        )
                    }

                    // Enhanced OpenStreetMap with custom marker
                    AndroidView(
                        factory = { ctx ->
                            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))

                            MapView(ctx).apply {
                                // Use a detailed tile source
                                setTileSource(TileSourceFactory.MAPNIK)

                                // Enable features for better detail
                                setBuiltInZoomControls(true)
                                setMultiTouchControls(true)
                                minZoomLevel = 12.0
                                maxZoomLevel = 21.0

                                // Set map center and zoom
                                controller.setCenter(GeoPoint(latitude, longitude))
                                controller.setZoom(18.0)

                                // Create custom marker
                                Marker(this).apply {
                                    position = GeoPoint(latitude, longitude)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = currentClinic.name

                                    // Set custom icon
                                    // Correct way - just use the Drawable directly
                                    ContextCompat.getDrawable(ctx, R.drawable.ic_location_marker)?.let { drawable ->
                                        setIcon(drawable)
                                    }


                                    // Enable info window
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
                                        20
                                    )
                                })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFE0E0E0))
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
            Text(text = feedback.patient_id.toString(), fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${feedback.title}/5", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = feedback.description ?: "", fontSize = 13.sp)
        }
    }
}

@Composable
fun AllReviewsScreen(doctorId: Int?, navController: NavController) {
    val feedbackRepository = remember { FeedbackRepository() }
    val feedbacksState = remember { mutableStateOf<List<Feedback>>(emptyList()) }

    LaunchedEffect(doctorId) {
        withContext(Dispatchers.IO) {
            doctorId?.let {
                feedbacksState.value = feedbackRepository.getFeedbacksByDoctorId(it)
            }
        }
    }

    val feedbacks = feedbacksState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (feedbacks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No reviews available", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(feedbacks) { feedback ->
                    FeedbackCard(feedback = feedback)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
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
