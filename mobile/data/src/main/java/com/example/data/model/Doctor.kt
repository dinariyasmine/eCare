package com.example.data.model

// Doctor.kt (Model)
data class Doctor(
    override   val id: Int ,
    override val name: String,
    override val email: String,
    override  val phone: String,
    override  val address: String,
    override  val role: String,
    override  val birth_date: String,
    val specialty: String,
    val clinic: String,
    val grade: Double,
    val description: String,
    val nbr_patients: Int,
    val clinic_pos  :String ?= null,
    val photo: String?,
    val facebook: String? = null,
    val twitter: String? = null,
    val linkedin: String? = null,
    val instagram: String? = null,
    val website: String? = null
): UserProfile
