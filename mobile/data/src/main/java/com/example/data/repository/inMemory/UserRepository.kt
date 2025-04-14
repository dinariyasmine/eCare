package com.example.data.repository

import android.annotation.SuppressLint
import com.example.data.model.Role
import com.example.data.model.User
import java.text.SimpleDateFormat
import java.util.*

class InMemoryUserRepository {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val users = mutableListOf<User>()

    init {
        // Initialize with dummy data
        try {
            users.addAll(
                listOf(
                    User(
                        id = 1,
                        name = "John Doe",
                        email = "john.doe@example.com",
                        password = "securePassword123",
                        phone = "+1-555-123-4567",
                        adress = "123 Main St, Springfield, USA",
                        role = Role.ADMIN,
                        birth_date = sdf.parse("1990-04-15")
                    ),
                    User(
                        id = 2,
                        name = "Jane Smith",
                        email = "jane.smith@example.com",
                        password = "strongPass456",
                        phone = "+1-555-987-6543",
                        adress = "456 Oak Ave, Metropolis, USA",
                        role = Role.DOCTOR,
                        birth_date = sdf.parse("1988-07-22")
                    ),
                    User(
                        id = 3,
                        name = "Alice Johnson",
                        email = "alice.johnson@example.com",
                        password = "alicePass789",
                        phone = "+1-555-456-7890",
                        adress = "789 Pine St, Gotham, USA",
                        role = Role.PATIENT,
                        birth_date = sdf.parse("1995-11-30")
                    )
                )
            )
        } catch (e: Exception) {
            // Handle date parsing exception
            println("Error initializing users: ${e.message}")
        }
    }

    fun getAllUsers(): List<User> {
        return users.toList()
    }

    fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    fun getUsersByRole(role: Role): List<User> {
        return users.filter { it.role == role }
    }

    fun createUser(user: User): Boolean {
        // Check if user with the same ID or email already exists
        if (users.any { it.id == user.id || it.email == user.email }) {
            return false
        }
        return users.add(user)
    }

    fun updateUser(user: User): Boolean {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
            return true
        }
        return false
    }

    fun deleteUser(id: Int): Boolean {
        val initialSize = users.size
        users.removeIf { it.id == id }
        return users.size < initialSize
    }

    fun searchUsersByName(query: String): List<User> {
        return users.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun formatDate(date: Date?): String {
        return if (date != null) sdf.format(date) else ""
    }

    fun parseDate(dateString: String): Date? {
        return try {
            sdf.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}
