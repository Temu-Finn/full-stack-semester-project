package edu.ntnu.idatt2105.gr2.backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class CreateUserRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String,

    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(
        min = 8,
        message = "Password must be at least 8 characters"
    )
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password cannot be empty")
    val password: String
)

data class UserResponse(
    val userId: Int,
    val name: String,
    val email: String,
    val joinedAt: Instant,
    val isAdmin: Boolean,
    val token: String,
    val expiresIn: Long,
)
