package com.example.exp1_s2_gustavo_renjifo.data

object UserRepository {
    private val users = mutableListOf(
        User(
            id = "1",
            username = "maria_silva",
            email = "maria.silva@email.com",
            password = "maria123",
            fullName = "María Silva González",
            phoneNumber = "+56912345678"
        ),
        User(
            id = "2",
            username = "carlos_mendez",
            email = "carlos.mendez@email.com",
            password = "carlos456",
            fullName = "Carlos Méndez López",
            phoneNumber = "+56923456789"
        ),
        User(
            id = "3",
            username = "ana_rodriguez",
            email = "ana.rodriguez@email.com",
            password = "ana789",
            fullName = "Ana Rodríguez Pérez",
            phoneNumber = "+56934567890"
        ),
        User(
            id = "4",
            username = "jose_torres",
            email = "jose.torres@email.com",
            password = "jose321",
            fullName = "José Torres Morales",
            phoneNumber = "+56945678901"
        ),
        User(
            id = "5",
            username = "lucia_ramirez",
            email = "lucia.ramirez@email.com",
            password = "lucia654",
            fullName = "Lucía Ramírez Castro",
            phoneNumber = "+56956789012"
        )
    )

    fun getAllUsers(): List<User> = users.toList()

    fun findUserByEmailOrUsername(identifier: String): User? {
        return users.find { 
            it.email.equals(identifier, ignoreCase = true) || 
            it.username.equals(identifier, ignoreCase = true) 
        }
    }

    fun authenticateUser(identifier: String, password: String): User? {
        val user = findUserByEmailOrUsername(identifier)
        return if (user?.password == password) user else null
    }

    fun addUser(user: User): Boolean {
        return if (findUserByEmailOrUsername(user.email) == null && 
                   findUserByEmailOrUsername(user.username) == null) {
            users.add(user.copy(id = (users.size + 1).toString()))
            true
        } else {
            false
        }
    }

    fun updateUserPassword(identifier: String, newPassword: String): Boolean {
        val userIndex = users.indexOfFirst { 
            it.email.equals(identifier, ignoreCase = true) || 
            it.username.equals(identifier, ignoreCase = true) 
        }
        return if (userIndex != -1) {
            users[userIndex] = users[userIndex].copy(password = newPassword)
            true
        } else {
            false
        }
    }
}