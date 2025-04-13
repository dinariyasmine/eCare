package com.example.data.repository

import com.example.data.model.Doctor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DoctorRepository {
    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Doctor> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            Doctor(
                id = 1,
                user_id = 101,
                photo = "https://example.com/photos/doctor1.jpg",
                specialty = "Cardiology",
                clinic_id = 1,
                grade = 4.8f,
                description = "Experienced cardiologist with over 15 years of practice. Specializes in interventional cardiology and heart disease prevention.",
                nbr_patients = 342
            ),
            Doctor(
                id = 2,
                user_id = 102,
                photo = "https://example.com/photos/doctor2.jpg",
                specialty = "Neurology",
                clinic_id = 2,
                grade = 4.6f,
                description = "Board-certified neurologist specializing in headache disorders, multiple sclerosis, and general neurology.",
                nbr_patients = 278
            ),
            Doctor(
                id = 3,
                user_id = 103,
                photo = "https://example.com/photos/doctor3.jpg",
                specialty = "Pediatrics",
                clinic_id = 1,
                grade = 4.9f,
                description = "Compassionate pediatrician dedicated to providing comprehensive care for children from birth through adolescence.",
                nbr_patients = 415
            )
        )
    }

    suspend fun getAllDoctors(): List<Doctor> {
        return simulateDatabaseCall()
    }

    suspend fun getDoctorById(id: Int): Doctor? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getDoctorByUserId(userId: Int): Doctor? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.user_id == userId }
    }

    suspend fun getDoctorsByClinicId(clinicId: Int): List<Doctor> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.clinic_id == clinicId }
    }

    suspend fun getDoctorsBySpecialty(specialty: String): List<Doctor> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.specialty.equals(specialty, ignoreCase = true) }
    }

    suspend fun createDoctor(doctor: Doctor): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        val existingDoctors = simulateDatabaseCall()
        return !existingDoctors.any { it.id == doctor.id || it.user_id == doctor.user_id }
    }

    suspend fun updateDoctor(doctor: Doctor): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == doctor.id }
    }

    suspend fun deleteDoctor(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }

    suspend fun searchDoctorsByName(name: String, userRepository: UserRepository): List<Doctor> {
        delay(500) // Simulate delay
        // This is a simplified implementation that doesn't actually use the userRepository
        // In a real app, you would query the user table and then join with doctors
        return simulateDatabaseCall().filter {
            // This is just a placeholder - in reality, you'd need to join with user data
            val doctorId = it.id
            doctorId % 2 == 0 // Just a dummy condition for simulation
        }
    }

    suspend fun getTopRatedDoctors(limit: Int = 5): List<Doctor> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().sortedByDescending { it.grade }.take(limit)
    }

    suspend fun getMostExperiencedDoctors(limit: Int = 5): List<Doctor> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().sortedByDescending { it.nbr_patients }.take(limit)
    }
}
