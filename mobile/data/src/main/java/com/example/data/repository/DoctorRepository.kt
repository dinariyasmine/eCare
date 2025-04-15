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

    fun getAllDoctors(): List<Doctor> {


        val doctors = listOf(
            Doctor(
                id = 1,
                user_id = 101,
                photo = "https://example.com/doctors/1.jpg",
                specialty = "Cardiology",
                clinic_id = 5,
                grade = 4.8f,
                description = "Experienced cardiologist with 15 years of practice",
                nbr_patients = 1243
            ),
            Doctor(
                id = 2,
                user_id = 102,
                photo = "https://example.com/doctors/2.jpg",
                specialty = "Pediatrics",
                clinic_id = 3,
                grade = 4.6f,
                description = "Pediatric specialist focusing on child wellness",
                nbr_patients = 876
            ),
            Doctor(
                id = 3,
                user_id = 103,
                photo = "https://example.com/doctors/3.jpg",
                specialty = "Neurology",
                clinic_id = 7,
                grade = 4.9f,
                description = "Neurology expert with research background",
                nbr_patients = 1542
            ),
            Doctor(
                id = 4,
                user_id = 104,
                photo = "https://example.com/doctors/4.jpg",
                specialty = "Dermatology",
                clinic_id = 2,
                grade = 4.7f,
                description = "Skin care specialist with cosmetic expertise",
                nbr_patients = 932
            ),
            Doctor(
                id = 5,
                user_id = 105,
                photo = "https://example.com/doctors/5.jpg",
                specialty = "Orthopedics",
                clinic_id = 8,
                grade = 4.5f,
                description = "Bone and joint specialist with surgical focus",
                nbr_patients = 1120
            ),
            Doctor(
                id = 6,
                user_id = 106,
                photo = "https://example.com/doctors/6.jpg",
                specialty = "General Practice",
                clinic_id = 1,
                grade = 4.3f,
                description = "Family doctor with holistic approach",
                nbr_patients = 2105
            ),
            Doctor(
                id = 7,
                user_id = 107,
                photo = "https://example.com/doctors/7.jpg",
                specialty = "Ophthalmology",
                clinic_id = 4,
                grade = 4.8f,
                description = "Eye care specialist with laser surgery expertise",
                nbr_patients = 1432
            ),
            Doctor(
                id = 8,
                user_id = 108,
                photo = "https://example.com/doctors/8.jpg",
                specialty = "Psychiatry",
                clinic_id = 6,
                grade = 4.4f,
                description = "Mental health professional with therapy focus",
                nbr_patients = 765
            )
        )
        return doctors
    }

    fun getDoctorById(id: Int): Doctor? {
//
        return  Doctor(
            id = 8,
            user_id = 108,
            photo = "https://example.com/doctors/8.jpg",
            specialty = "Psychiatry",
            clinic_id = 6,
            grade = 4.4f,
            description = "Mental health professional with therapy focus",
            nbr_patients = 765
        )
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
