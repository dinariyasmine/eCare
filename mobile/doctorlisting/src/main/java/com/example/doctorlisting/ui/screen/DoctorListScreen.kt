package com.example.doctorlisting.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.adamglin.PhosphorIcons
import com.example.doctorlisting.R
import com.example.data.model.Doctor
import com.example.data.viewModel.DoctorViewModel
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Star
import com.adamglin.phosphoricons.regular.FunnelSimple
import com.adamglin.phosphoricons.regular.MagnifyingGlass
import com.adamglin.phosphoricons.regular.CaretDown
import com.adamglin.phosphoricons.regular.MapPin
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository

@Composable
fun DoctorListScreen(
    navController: NavController,
    doctorViewModel: DoctorViewModel = viewModel(
        factory = DoctorViewModel.Factory(
            doctorRepository = DoctorRepository(),
            userRepository = UserRepository()
        )
    )
) {
    val doctors by doctorViewModel.doctors.collectAsState()
    val loading by doctorViewModel.loading.collectAsState()
    val error by doctorViewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        doctorViewModel.fetchAllDoctors()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        SearchBarWithFilter(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                doctorViewModel.searchDoctorsByName(searchQuery)
            },
            onFilterClick = { showFilterDialog = true }
        )

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (error != null) {
            Text(
                text = error ?: "An error occurred",
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Symptoms Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            listOf("Headache", "Nausea", "Fever", "Cold", "Palpitations").forEach { symptom ->
                Chip(text = symptom)
            }
        }

        LazyColumn {
            items(doctors) { doctor ->
                DoctorCard(doctor = doctor, navController = navController)
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onApply = {
                // implement filtering logic if needed
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun SearchBarWithFilter(
    value: String,
    onValueChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = PhosphorIcons.Regular.MagnifyingGlass,
                contentDescription = "Search",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "Search a doctor",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = PhosphorIcons.Regular.FunnelSimple,
                contentDescription = "Filter",
                tint = Color.Gray,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onFilterClick() }
            )
        }
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(max = 500.dp) // Définir une hauteur maximale pour la boîte de dialogue
            ) {
                // En-tête
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Text(
                        text = "Filter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    TextButton(onClick = onApply) {
                        Text("Apply", color = Color(0xFF2196F3))
                    }
                }

                // Rendre le contenu de la boîte de dialogue scrollable
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Section des spécialisations
                    Text(
                        text = "Specialisations",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // Checkboxes de spécialisation initialement visibles
                    var showAllSpecialties by remember { mutableStateOf(false) }
                    val initialSpecialties = listOf("Cardiology", "Dermatology", "Pediatrics", "Neurology")
                    val additionalSpecialties = listOf(
                        "Ophthalmology", "Orthopedics", "Psychiatry", "Gynecology", "Urology",
                        "Endocrinology", "Gastroenterology", "Hematology", "Nephrology", "Oncology",
                        "Pulmonology", "Rheumatology", "Allergy & Immunology"
                    )
                    // Afficher les spécialisations initiales
                    initialSpecialties.forEach { specialization ->
                        SpecialtyCheckbox(specialty = specialization)
                    }
                    // Afficher les spécialisations supplémentaires si elles sont étendues
                    if (showAllSpecialties) {
                        additionalSpecialties.forEach { specialization ->
                            SpecialtyCheckbox(specialty = specialization)
                        }
                    }
                    // Bouton "Afficher tout" avec flèche déroulante
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { showAllSpecialties = !showAllSpecialties },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showAllSpecialties) "Show less" else "Show all",
                            color = Color(0xFF2196F3),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = PhosphorIcons.Regular.CaretDown,
                            contentDescription = "Expand",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(if (showAllSpecialties) 180f else 0f)
                        )
                    }
                    Divider()
                    // Section des avis
                    Text(
                        text = "Reviews",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            var selected by remember { mutableStateOf(false) }
                            Icon(
                                imageVector = PhosphorIcons.Regular.Star,
                                contentDescription = "Star ${index + 1}",
                                tint = if (selected) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { selected = !selected }
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    // Section de la localisation
                    Text(
                        text = "Location",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LocationChip(text = "Near me", selected = true)
                        LocationChip(text = "My City", selected = false)
                        LocationChip(text = "All", selected = false)
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    // Section des patients traités
                    Text(
                        text = "Patients Treated",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PatientCountChip(text = "< 200")
                        PatientCountChip(text = "500-1000")
                        PatientCountChip(text = "> 1000")
                    }
                }
            }
        }
    }
}

@Composable
fun SpecialtyCheckbox(specialty: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var checked by remember { mutableStateOf(false) }
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF2196F3)
            )
        )
        Text(text = specialty)
    }
}

@Composable
fun LocationChip(text: String, selected: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF2196F3) else Color(0xFFF5F5F5)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun PatientCountChip(text: String) {
    var selected by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF2196F3) else Color(0xFFF5F5F5),
        modifier = Modifier.clickable { selected = !selected }
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun Chip(text: String) {
    Surface(
        modifier = Modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun DoctorCard(doctor: Doctor, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("doctor/${doctor.id}") },
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Image du docteur
            Image(
                painter = rememberImagePainter(data = doctor.photo), // Utilisation de doctor.photo [4, 5]
                contentDescription = "Doctor Image",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Infos du docteur
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = doctor.specialty, fontWeight = FontWeight.Bold, fontSize = 18.sp) // Affichage de la spécialité [4, 5]
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = PhosphorIcons.Regular.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(text = doctor.grade.toString(), fontSize = 14.sp) // Affichage de la note [4, 5]
                }
                Text(text = doctor.specialty, color = Color.Gray, fontSize = 14.sp) // Redondant, déjà affiché au-dessus
                Text(
                    text = doctor.description, // Utilisation de la description du docteur [4, 5]
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2 // Limiter le nombre de lignes pour la description
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = PhosphorIcons.Regular.MapPin,
                        contentDescription = "Location",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "2.4 km away", fontSize = 12.sp, color = Color.Gray) // La distance n'est pas dans le modèle
                }
            }
        }
    }
}