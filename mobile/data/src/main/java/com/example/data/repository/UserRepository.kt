package com.example.data.repository


import com.example.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UserRepository {
    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<User> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        try {
            return@withContext listOf(
                User(
                    id = 1,
                    name = "John Doe",
                    email = "john.doe@example.com",
                    password = "securePassword123",
                    phone = "+1-555-123-4567",
                    address = "123 Main St, Springfield, USA",
                    role = "admin",
                    birth_date = sdf.parse("1990-04-15")
                ),
                User(
                    id = 2,
                    name = "Jane Smith",
                    email = "jane.smith@example.com",
                    password = "strongPass456",
                    phone = "+1-555-987-6543",
                    address = "456 Oak Ave, Metropolis, USA",
                    role = "doctor",
                    birth_date = sdf.parse("1988-07-22")
                ),
                User(
                    id = 3,
                    name = "Alice Johnson",
                    email = "alice.johnson@example.com",
                    password = "alicePass789",
                    phone = "+1-555-456-7890",
                    address = "789 Pine St, Gotham, USA",
                    role = "patient",
                    birth_date = sdf.parse("1995-11-30")
                )
            )
        } catch (e: Exception) {
            // Handle date parsing exception
            println("Error creating users: ${e.message}")
            return@withContext emptyList<User>()
        }
    }

    suspend fun getAllUsers(): List<User> {
        return  listOf(
            User(
                id = 1,
                name = "John Doe",
                email = "john.doe@example.com",
                password = "securePassword123",
                phone = "+1-555-123-4567",
                address = "123 Main St, Springfield, USA",
                role = "admin",
                birth_date = sdf.parse("1990-04-15")
            ),
            User(
                id = 2,
                name = "Jane Smith",
                email = "jane.smith@example.com",
                password = "strongPass456",
                phone = "+1-555-987-6543",
                address = "456 Oak Ave, Metropolis, USA",
                role = "doctor",
                birth_date = sdf.parse("1988-07-22")
            ),
            User(
                id = 3,
                name = "Alice Johnson",
                email = "alice.johnson@example.com",
                password = "alicePass789",
                phone = "+1-555-456-7890",
                address = "789 Pine St, Gotham, USA",
                role = "patient",
                birth_date = sdf.parse("1995-11-30")
            )
        )
    }

    suspend fun getUserById(id: Int): User? {
        return   User(
            id = 3,
            name = "Alice Johnson",
            email = "alice.johnson@example.com",
            password = "alicePass789",
            phone = "+1-555-456-7890",
            address = "789 Pine St, Gotham, USA",
            role = "patient",
            birth_date = sdf.parse("1995-11-30")
        )
    }

    suspend fun getUserByEmail(email: String): User? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.email == email }
    }

    suspend fun getUsersByRole(role: String): List<User> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.role == role }
    }

    suspend fun createUser(user: User): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        val existingUsers = simulateDatabaseCall()
        return !existingUsers.any { it.id == user.id || it.email == user.email }
    }

    suspend fun updateUser(user: User): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == user.id }
    }

    suspend fun deleteUser(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }

    suspend fun searchUsersByName(query: String): List<User> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.name.contains(query, ignoreCase = true) }
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
