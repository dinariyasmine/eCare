package com.example.ecare_mobile.data.repository

import com.example.ecare_mobile.data.model.User

class UserRepository {
    fun getUsers(): List<User> {
        // Simulate fetching data from an API or database
        return listOf(
            User(
                1, "John Doe", "john.doe@example.com",
                avatarUrl =  "somwhere"
            ),
            User(
                2, "Jane Smith", "jane.smith@example.com",
                avatarUrl = "somwhere"
            )
        )
    }
 fun   getAvatarUrl() : String
    {
        return "somwhere"
    }
}
