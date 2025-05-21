package com.example.prescription.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.theme.Primary500
import com.example.data.viewModel.PrescriptionViewModel
import com.example.prescription.ui.components.PrescriptionCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionListScreen(
    navigateToPrescriptionDetail: (Int) -> Unit,
    navigateToCreatePrescription: () -> Unit,
    viewModel: PrescriptionViewModel = viewModel(),
    isDoctor: Boolean = false
) {
    val prescriptions by viewModel.prescriptions.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.fetchPrescriptions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescriptions", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary500,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (isDoctor) {
                FloatingActionButton(
                    onClick = { navigateToCreatePrescription() },
                    containerColor = Primary500,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Prescription")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary500
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${error ?: "Unknown error"}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchPrescriptions() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary500
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
                prescriptions.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No prescriptions found",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Today",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val today = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time

                        val todayPrescriptions = prescriptions.filter {
                            val prescriptionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
                            prescriptionDate?.compareTo(today) == 0
                        }

                        if (todayPrescriptions.isEmpty()) {
                            item {
                                Text(
                                    text = "No prescriptions for today",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                                )
                            }
                        } else {
                            items(todayPrescriptions) { prescription ->
                                PrescriptionCard(
                                    prescription = prescription,
                                    onClick = { navigateToPrescriptionDetail(prescription.id) }
                                )
                            }
                        }

                        item {
                            Text(
                                text = "One week ago",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val oneWeekAgo = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, -7)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time

                        val oneWeekPrescriptions = prescriptions.filter {
                            val prescriptionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
                            prescriptionDate?.before(today) == true && prescriptionDate.after(oneWeekAgo)
                        }

                        if (oneWeekPrescriptions.isEmpty()) {
                            item {
                                Text(
                                    text = "No prescriptions from last week",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                                )
                            }
                        } else {
                            items(oneWeekPrescriptions) { prescription ->
                                PrescriptionCard(
                                    prescription = prescription,
                                    onClick = { navigateToPrescriptionDetail(prescription.id) }
                                )
                            }
                        }

                        item {
                            Text(
                                text = "A month ago",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val earlierPrescriptions = prescriptions.filter {
                            val prescriptionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
                            prescriptionDate?.before(oneWeekAgo) ?: false
                        }

                        if (earlierPrescriptions.isEmpty()) {
                            item {
                                Text(
                                    text = "No older prescriptions",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                                )
                            }
                        } else {
                            items(earlierPrescriptions) { prescription ->
                                PrescriptionCard(
                                    prescription = prescription,
                                    onClick = { navigateToPrescriptionDetail(prescription.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
