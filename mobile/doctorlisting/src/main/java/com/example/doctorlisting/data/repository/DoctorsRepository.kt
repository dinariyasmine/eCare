package com.example.doctorlisting.data.repository

// shared/src/commonMain/kotlin/com/example/shared/data/DoctorsRepository.kt

import com.example.doctorlisting.data.model.Doctor

interface DoctorsRepository {
    fun getDoctors(): List<Doctor>
    fun getDoctorById(id: Int): Doctor?
}

class DoctorsRepositoryImpl() : DoctorsRepository {
    override fun getDoctors(): List<Doctor> {
        return DoctorDataSource.getDoctors()
    }
    override fun getDoctorById(id: Int): Doctor? {
        return DoctorDataSource.getDoctorById(id)
    }
}