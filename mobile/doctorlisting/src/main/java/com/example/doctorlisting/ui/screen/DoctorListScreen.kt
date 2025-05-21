
package com.example.doctorlisting.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
//noinspection UsingMaterialAndMaterial3Libraries
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
import com.example.data.network.ApiClient
import com.example.data.viewModel.DoctorViewModel
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DoctorListScreen(navController: NavController) {
    val repository = remember { DoctorRepository(ApiClient.apiService) }
    val doctorViewModel = remember { DoctorViewModel(repository) }


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
            onApply = { specialties, minRating, patientRange ->
                doctorViewModel.resetFilters()

                // Apply specialty filters
                specialties.forEach { (specialty, isSelected) ->
                    if (isSelected) {
                        doctorViewModel.updateSpecialtyFilter(specialty, true)
                    }
                }

                // Apply rating filter
                doctorViewModel.updateRatingFilter(minRating)

                // Apply patient count filter
                val (minPatients, maxPatients) = patientRange
                if (minPatients != null && maxPatients != null) {
                    doctorViewModel.updatePatientCountFilter(minPatients, maxPatients)
                }

                showFilterDialog = false
            }
        )
    }
}

@Composable
fun DoctorCard(doctor: Doctor, navController: NavController) {
    val doctorId = doctor.id ?: "unknown"
    val specialty = doctor.specialty ?: "Unknown Specialty"
    val grade = doctor.grade ?: 0f
    val description = doctor.description ?: "No description available"
    val nbrPatients = doctor.nbr_patients ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("doctor/$doctorId") },
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Doctor image (using doctorId or fallback)
            Image(
                painter = rememberImagePainter(data = doctorId),
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
                    Text(text = specialty, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = PhosphorIcons.Regular.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(text = grade.toString(), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
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
                        text = "$nbrPatients patients",
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
    var selectedRating by mutableStateOf(0f)
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
    onApply: (Map<String, Boolean>, Float, Pair<Int?, Int?>) -> Unit
) {
    val selectedRating = remember { mutableStateOf(filterState.selectedRating) }
    val patientRange = remember { mutableStateOf(filterState.selectedPatientRange) }
    val minPatients = remember { mutableStateOf(patientRange.value.first ?: 0) }
    val maxPatients = remember { mutableStateOf(patientRange.value.second ?: 1000) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Header with Cancel and Apply buttons
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
                            patientRange.value = minPatients.value to maxPatients.value
                            filterState.selectedRating = selectedRating.value
                            filterState.selectedPatientRange = patientRange.value
                            onApply(
                                filterState.specialtySelections.toMap(),
                                selectedRating.value,
                                patientRange.value
                            )
                        }
                    ) {
                        Text("Apply", color = Color(0xFF2196F3))
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Content with scrolling
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Specialisations section
                    Text(
                        "Specialisations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

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

                    ShowMoreButton(
                        expanded = showAllSpecialties,
                        onClick = { showAllSpecialties = !showAllSpecialties }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Rating section
                    Text(
                        "Reviews",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    RatingSelector(
                        currentRating = selectedRating.value,
                        onRatingChanged = { selectedRating.value = it }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Patient range section
                    Text(
                        "Number of Patients",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    PatientRangeSelector(
                        minPatients = minPatients.value,
                        maxPatients = maxPatients.value,
                        onMinPatientsChange = { minPatients.value = it },
                        onMaxPatientsChange = { maxPatients.value = it }
                    )
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
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF2196F3),
                uncheckedColor = Color.LightGray
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(specialty, fontSize = 14.sp)
    }
}

@Composable
fun ShowMoreButton(expanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (expanded) "Show less" else "Show all",
            color = Color(0xFF2196F3),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = PhosphorIcons.Regular.CaretDown,
            contentDescription = if (expanded) "Show less" else "Show more",
            modifier = Modifier
                .size(16.dp)
                .rotate(if (expanded) 180f else 0f),
            tint = Color(0xFF2196F3)
        )
    }
}

@Composable
fun RatingSelector(currentRating: Float, onRatingChanged: (Float) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = PhosphorIcons.Regular.Star,
                contentDescription = "Rating $i",
                tint = if (i <= currentRating) Color(0xFFFFC107) else Color.LightGray,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChanged(i.toFloat()) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun PatientRangeSelector(
    minPatients: Int,
    maxPatients: Int,
    onMinPatientsChange: (Int) -> Unit,
    onMaxPatientsChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Min: $minPatients", fontSize = 14.sp)
            Text("Max: $maxPatients", fontSize = 14.sp)
        }

        // Patient Range Slider
        val sliderPosition = remember {
            mutableStateOf(minPatients.toFloat()..maxPatients.toFloat())
        }

        Slider(
            value = sliderPosition.value.endInclusive,
            onValueChange = { newValue ->
                val newRange = sliderPosition.value.start..newValue
                sliderPosition.value = newRange
                onMaxPatientsChange(newValue.toInt())
            },
            valueRange = 0f..1000f,
            steps = 19,  // 20 positions (0, 50, 100, ..., 1000)
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF2196F3),
                activeTrackColor = Color(0xFF2196F3),
                inactiveTrackColor = Color.LightGray
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        Slider(
            value = sliderPosition.value.start,
            onValueChange = { newValue ->
                // Ensure min doesn't exceed max
                val validValue = minOf(newValue, sliderPosition.value.endInclusive - 50f)
                val newRange = validValue..sliderPosition.value.endInclusive
                sliderPosition.value = newRange
                onMinPatientsChange(validValue.toInt())
            },
            valueRange = 0f..1000f,
            steps = 19,  // 20 positions (0, 50, 100, ..., 1000)
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF2196F3),
                activeTrackColor = Color(0xFF2196F3),
                inactiveTrackColor = Color.LightGray
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0", fontSize = 12.sp, color = Color.Gray)
            Text("1000", fontSize = 12.sp, color = Color.Gray)
        }
    }
}