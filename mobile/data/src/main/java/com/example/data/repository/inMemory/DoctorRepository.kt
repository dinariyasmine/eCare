package com.example.data.repository.inMemory

import com.example.data.model.Doctor
import com.example.data.repository.UserRepository

class InMemoryDoctorRepository {
    private val doctors = mutableListOf<Doctor>()

    init {
        // Initialize with dummy data
        doctors.addAll(
            listOf(
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
        )
    }

    fun getAllDoctors(): List<Doctor> {
        return doctors.toList()
    }

    fun getDoctorById(id: Int): Doctor? {
        return doctors.find { it.id == id }
    }

    fun getDoctorByUserId(userId: Int): Doctor? {
        return doctors.find { it.user_id == userId }
    }

    fun getDoctorsByClinicId(clinicId: Int): List<Doctor> {
        return doctors.filter { it.clinic_id == clinicId }
    }

    fun getDoctorsBySpecialty(specialty: String): List<Doctor> {
        return doctors.filter { it.specialty.equals(specialty, ignoreCase = true) }
    }

    fun createDoctor(doctor: Doctor): Boolean {
        // Check if doctor with the same ID or user_id already exists
        if (doctors.any { it.id == doctor.id || it.user_id == doctor.user_id }) {
            return false
        }
        return doctors.add(doctor)
    }

    fun updateDoctor(doctor: Doctor): Boolean {
        val index = doctors.indexOfFirst { it.id == doctor.id }
        if (index != -1) {
            doctors[index] = doctor
            return true
        }
        return false
    }

    fun deleteDoctor(id: Int): Boolean {
        val initialSize = doctors.size
        doctors.removeIf { it.id == id }
        return doctors.size < initialSize
    }

    suspend fun searchDoctorsByName(name: String, userRepository: UserRepository): List<Doctor> {
        val users = userRepository.getAllUsers()
        val matchingUserIds = users
            .filter { it.name.contains(name, ignoreCase = true) }
            .map { it.id }

        return doctors.filter { it.user_id in matchingUserIds }
    }

    fun getTopRatedDoctors(limit: Int = 5): List<Doctor> {
        return doctors.sortedByDescending { it.grade }.take(limit)
    }

    fun getMostExperiencedDoctors(limit: Int = 5): List<Doctor> {
        return doctors.sortedByDescending { it.nbr_patients }.take(limit)
    }
}
