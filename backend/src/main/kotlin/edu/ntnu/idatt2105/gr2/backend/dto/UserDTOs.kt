package edu.ntnu.idatt2105.gr2.backend.dto

import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class UserProfile(
    val id: Int,
    val email: String,
    val name: String,
    val joinedAt: Instant,
    val isAdmin: Boolean
)

data class UpdateEmailRequest(
    @field:NotBlank(message = "Email cannot be empty")
    val email: String
)

data class UpdateNameRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String
)