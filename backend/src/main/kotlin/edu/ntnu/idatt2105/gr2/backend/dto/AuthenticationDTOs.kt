package edu.ntnu.idatt2105.gr2.backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

/**
 * Data Transfer Objects (DTOs) for user authentication and profile management.
 * Used for transferring data between the backend and frontend.
 */

data class CreateUserRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String,
    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Invalid email format")
    val email: String,
    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(
        min = 8,
        message = "Password must be at least 8 characters",
    )
    val password: String,
)

data class LoginRequest(
    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Invalid email format")
    val email: String,
    @field:NotBlank(message = "Password cannot be empty")
    val password: String,
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

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password cannot be empty")
    val currentPassword: String,
    @field:NotBlank(message = "New password cannot be empty")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val newPassword: String,
)
