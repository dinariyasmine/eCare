
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

    // Filter state to be passed to dialog
    val filterState = remember { FilterState() }

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
            val symptoms = listOf("Headache", "Nausea", "Fever", "Cold", "Palpitations")
            symptoms.forEach { symptom ->
                Chip(
                    text = symptom,
                    onClick = { doctorViewModel.filterBySymptom(symptom) }
                )
            }
        }

        LazyColumn {
            items(doctors) { doctor ->
                DoctorCard(doctor = doctor, navController = navController)
            }

            // Show message when no doctors match filters
            if (doctors.isEmpty() && !loading) {
                item {
                    Text(
                        text = "No doctors found matching your criteria",
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            filterState = filterState,
            onDismiss = { showFilterDialog = false },
            onApply = { specialties, minRating, location, patientRange ->
                // Apply all filters
                doctorViewModel.resetFilters()

                // Apply specialties filter
                specialties.forEach { (specialty, isSelected) ->
                    if (isSelected) {
                        doctorViewModel.updateSpecialtyFilter(specialty, true)
                    }
                }

                // Apply rating filter
                doctorViewModel.updateRatingFilter(minRating)

                // Apply location filter
                if (location != null) {
                    doctorViewModel.updateLocationFilter(location)
                }

                // Apply patient count filter
                val (minPatients, maxPatients) = patientRange
                doctorViewModel.updatePatientCountFilter(minPatients, maxPatients)

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

// State holder for filter dialog
class FilterState {
    val specialtySelections = mutableStateMapOf<String, Boolean>()
    var selectedRating by mutableStateOf(0)
    var selectedLocation by mutableStateOf<String?>(null)
    var selectedPatientRange by mutableStateOf<Pair<Int?, Int?>>(null to null)

    // Initialize with default specialties
    init {
        listOf(
            "Cardiology", "Dermatology", "Pediatrics", "Neurology",
            "Ophthalmology", "Orthopedics", "Psychiatry", "Gynecology", "Urology",
            "Endocrinology", "Gastroenterology", "Hematology", "Nephrology", "Oncology",
            "Pulmonology", "Rheumatology", "General Practice", "Allergy & Immunology"
        ).forEach { specialty ->
            specialtySelections[specialty] = false
        }
    }
}

@Composable
fun FilterDialog(
    filterState: FilterState,
    onDismiss: () -> Unit,
    onApply: (Map<String, Boolean>, Float, String?, Pair<Int?, Int?>) -> Unit
) {
    var selectedRating by remember { mutableStateOf(filterState.selectedRating) }
    var selectedLocation by remember { mutableStateOf(filterState.selectedLocation) }
    var patientRange by remember { mutableStateOf(filterState.selectedPatientRange) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                // Header
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
                    TextButton(
                        onClick = {
                            onApply(
                                filterState.specialtySelections.toMap(),
                                selectedRating.toFloat(),
                                selectedLocation,
                                patientRange
                            )
                        }
                    ) {
                        Text("Apply", color = Color(0xFF2196F3))
                    }
                }

                // Make the content of the dialog scrollable
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Specializations section
                    Text(
                        text = "Specialisations",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Show specialties with checkboxes
                    var showAllSpecialties by remember { mutableStateOf(false) }
                    val initialSpecialties = listOf("Cardiology", "Dermatology", "Pediatrics", "Neurology")
                    val additionalSpecialties = filterState.specialtySelections.keys.toList()
                        .filter { it !in initialSpecialties }

                    // Show initial specialties
                    initialSpecialties.forEach { specialty ->
                        SpecialtyCheckbox(
                            specialty = specialty,
                            checked = filterState.specialtySelections[specialty] ?: false,
                            onCheckedChange = { checked ->
                                filterState.specialtySelections[specialty] = checked
                            }
                        )
                    }

                    // Show additional specialties if expanded
                    if (showAllSpecialties) {
                        additionalSpecialties.forEach { specialty ->
                            SpecialtyCheckbox(
                                specialty = specialty,
                                checked = filterState.specialtySelections[specialty] ?: false,
                                onCheckedChange = { checked ->
                                    filterState.specialtySelections[specialty] = checked
                                }
                            )
                        }
                    }

                    // Show all toggle with dropdown arrow
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

                    // Reviews section
                    Text(
                        text = "Reviews",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = PhosphorIcons.Regular.Star,
                                contentDescription = "Star ${index + 1}",
                                tint = if (index < selectedRating) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { selectedRating = index + 1 }
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    // Location section
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
                        LocationChip(
                            text = "Near me",
                            selected = selectedLocation == "Near me",
                            onClick = { selectedLocation = "Near me" }
                        )
                        LocationChip(
                            text = "My City",
                            selected = selectedLocation == "My City",
                            onClick = { selectedLocation = "My City" }
                        )
                        LocationChip(
                            text = "All",
                            selected = selectedLocation == "All" || selectedLocation == null,
                            onClick = { selectedLocation = "All" }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    // Patients Treated section
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
                        PatientCountChip(
                            text = "< 200",
                            selected = patientRange.first == 0 && patientRange.second == 200,
                            onClick = {
                                patientRange = if (patientRange.first == 0 && patientRange.second == 200) {
                                    null to null  // Toggle off
                                } else {
                                    0 to 200
                                }
                            }
                        )
                        PatientCountChip(
                            text = "500-1000",
                            selected = patientRange.first == 500 && patientRange.second == 1000,
                            onClick = {
                                patientRange = if (patientRange.first == 500 && patientRange.second == 1000) {
                                    null to null  // Toggle off
                                } else {
                                    500 to 1000
                                }
                            }
                        )
                        PatientCountChip(
                            text = "> 1000",
                            selected = patientRange.first == 1000 && patientRange.second == null,
                            onClick = {
                                patientRange = if (patientRange.first == 1000 && patientRange.second == null) {
                                    null to null  // Toggle off
                                } else {
                                    1000 to null
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecialtyCheckbox(
    specialty: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF2196F3)
            )
        )
        Text(text = specialty)
    }
}

@Composable
fun LocationChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF2196F3) else Color(0xFFF5F5F5),
        modifier = Modifier.clickable(onClick = onClick)
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
fun PatientCountChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF2196F3) else Color(0xFFF5F5F5),
        modifier = Modifier.clickable(onClick = onClick)
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
fun Chip(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable(onClick = onClick),
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
            // Doctor image
            Image(
                painter = rememberImagePainter(data = doctor.photo),
                contentDescription = "Doctor Image",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Doctor info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = doctor.specialty, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = PhosphorIcons.Regular.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(text = doctor.grade.toString(), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doctor.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
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
                    Text(text = "2.4 km away", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${doctor.nbr_patients} patients",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}