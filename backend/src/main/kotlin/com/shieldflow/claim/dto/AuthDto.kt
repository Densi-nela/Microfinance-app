package com.shieldflow.claim.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class LoginRequest(
    @get:NotBlank(message = "Email is required")
    @get:Email(message = "Please provide a valid email address")
    val email: String,

    @get:NotBlank(message = "Password is required")
    val password: String
)

data class RegisterRequest(
    @get:NotBlank(message = "Email is required")
    @get:Email(message = "Please provide a valid email address")
    val email: String,

    @get:NotBlank(message = "Password is required")
    @get:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String,

    @get:NotBlank(message = "Full name is required")
    val fullName: String,

    val role: String = "CUSTOMER" // 'CUSTOMER' or 'ADMIN'
)

data class AuthResponse(
    val token: String,
    val id: Long,
    val email: String,
    val fullName: String,
    val role: String
)
