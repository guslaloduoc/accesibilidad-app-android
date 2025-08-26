package com.example.exp1_s2_gustavo_renjifo.data

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String = "",
    val hasVisualImpairment: Boolean = true
)