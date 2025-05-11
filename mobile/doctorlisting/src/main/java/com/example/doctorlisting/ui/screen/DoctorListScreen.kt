package com.example.doctorlisting.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.*
import com.example.data.model.Doctor
import com.example.data.viewModel.DoctorViewModel
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DoctorListScreen(navController: NavController) {
    val doctorViewModel = remember { DoctorViewModel() }

    val doctors by doctorViewModel.doctors.collectAsState()
    val loading by doctorViewModel.loading.collectAsState()
    val error by doctorViewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    val filterState = remember { FilterState() }

    LaunchedEffect(Unit) {
        doctorViewModel.getDoctorsFromApi()
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

        LazyColumn {
            items(doctors) { doctor ->
                DoctorCard(doctor = doctor, navController = navController)
            }

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
                doctorViewModel.resetFilters()
                specialties.forEach { (specialty, isSelected) ->
                    if (isSelected) {
                       // doctorViewModel.updateSpecialtyFilter(specialty, true)
                    }
                }
              //  doctorViewModel.updateRatingFilter(minRating)
              //  location?.let { doctorViewModel.updateLocationFilter(it) }
                val (minPatients, maxPatients) = patientRange
                if (minPatients != null && maxPatients != null) {
                  //  doctorViewModel.updatePatientCountFilter(minPatients, maxPatients)
                }
                showFilterDialog = false
            }
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
                painter = rememberImagePainter(data = doctor.id),
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
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text("Search a doctor", color = Color.Gray, fontSize = 14.sp)
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

class FilterState {
    val specialtySelections = mutableStateMapOf<String, Boolean>()
    var selectedRating by mutableStateOf(0)
    var selectedLocation by mutableStateOf<String?>(null)
    var selectedPatientRange by mutableStateOf<Pair<Int?, Int?>>(null to null)

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Text("Filter", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Specialisations", fontSize = 14.sp, color = Color.Gray)

                    var showAllSpecialties by remember { mutableStateOf(false) }
                    val initialSpecialties = listOf("Cardiology", "Dermatology", "Pediatrics", "Neurology")
                    val additionalSpecialties = filterState.specialtySelections.keys
                        .filterNot { it in initialSpecialties }

                    initialSpecialties.forEach { specialty ->
                        SpecialtyCheckbox(
                            specialty = specialty,
                            checked = filterState.specialtySelections[specialty] ?: false,
                            onCheckedChange = { filterState.specialtySelections[specialty] = it }
                        )
                    }

                    if (showAllSpecialties) {
                        additionalSpecialties.forEach { specialty ->
                            SpecialtyCheckbox(
                                specialty = specialty,
                                checked = filterState.specialtySelections[specialty] ?: false,
                                onCheckedChange = { filterState.specialtySelections[specialty] = it }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAllSpecialties = !showAllSpecialties }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (showAllSpecialties) "Show less" else "Show all",
                            color = Color(0xFF2196F3),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = PhosphorIcons.Regular.CaretDown,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(if (showAllSpecialties) 180f else 0f),
                            tint = Color(0xFF2196F3)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecialtyCheckbox(specialty: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(specialty)
    }
}
