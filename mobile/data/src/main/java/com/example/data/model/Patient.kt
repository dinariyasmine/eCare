package com.example.data.model

data class Patient(
    override val id: Int,
    override   val name: String,
    override  val email: String,
    override  val phone: String,
    override val address: String,
    override val role: String,
    override val birth_date: String,

    ): UserProfile