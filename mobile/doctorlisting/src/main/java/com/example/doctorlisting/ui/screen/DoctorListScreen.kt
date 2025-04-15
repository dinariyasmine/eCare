import com.example.doctorlisting.ui.screen.DoctorDetailScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.Doctor
import com.example.data.model.User

@Composable
fun DoctorListScreen(
    doctors: List<Doctor> = emptyList(),
    doctorUsers: Map<Int, User> = emptyMap() // Map of doctor's user_id to User
) {
    var searchQuery by remember { mutableStateOf("") }
    val symptoms = listOf("Headache", "Nausea", "Fever", "Cold", "Palpitation")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search a doctor") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Your symptoms section
        Text(
            text = "Your symptoms",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Symptoms chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(symptoms) { symptom ->
                SuggestedSymptomChip(symptom = symptom)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Doctors list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(doctors) { doctor ->
                val user = doctorUsers[doctor.user_id]
                if (user != null) {
                    DoctorCard(doctor = doctor, user = user)
                }
            }
        }
    }
}

@Composable
fun SuggestedSymptomChip(symptom: String) {
    Surface(
        color = Color(0xFFF3F4F6),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symptom,
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Doctor image
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            ) {
                // If you have real images, use the doctor.photo URL with Coil
                // For the sample, we use a placeholder
                Image(
                    painter = rememberAsyncImagePainter(model = doctor.photo),
                    contentDescription = "Doctor photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Doctor name
                Text(
                    text = "Dr." + user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Specialty
                Text(
                    text = doctor.specialty,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description (showing truncated)
                Text(
                    text = doctor.description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Distance info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "2.4 km away",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.btn_star_big_on),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = doctor.grade.toString(),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Preview function
@Composable
fun DoctorSearchScreenPreview() {
    // Sample data for preview
    val sampleDoctor = Doctor(
        id = 1,
        user_id = 1,
        photo = "",
        specialty = "Cardiologist",
        clinic_id = 1,
        grade = 4.6f,
        description = "Lorem ipsum dolor, consectetur adipiscing elit. Nulla nec diam nec interdum ac nulla.",
        nbr_patients = 120
    )

    val sampleUser = User(
        id = 1,
        name = "Rachid",
        email = "dr.rachid@example.com",
        password = "password",
        phone = "+1234567890",
        adress = "123 Medical Center",
        role = com.example.data.model.Role.DOCTOR,
        birth_date = java.util.Date()
    )

    val doctorsList = listOf(sampleDoctor, sampleDoctor, sampleDoctor)
    val usersMap = mapOf(1 to sampleUser)

    DoctorListScreen(doctors = doctorsList, doctorUsers = usersMap)
}


