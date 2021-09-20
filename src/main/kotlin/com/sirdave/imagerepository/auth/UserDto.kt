package com.sirdave.imagerepository.auth

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val userName: String,
    val email: String,
    val password: String,
    val confirmPassword: String

)

data class SignInRequest(
    val email: String,
    val password: String,
)

