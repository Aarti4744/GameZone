package com.games.gamezone.Registration

data class RegisterRequest(
    val name: String,
    val age: Int,
    val email: String,
    val number: String,
    val password: String,
    val confirm_password: String
)