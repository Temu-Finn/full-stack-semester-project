package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.CreateUserRequest
import edu.ntnu.idatt2105.gr2.backend.service.AuthenticationService
import edu.ntnu.idatt2105.gr2.backend.dto.LoginRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UserResponse
import edu.ntnu.idatt2105.gr2.backend.model.User
import edu.ntnu.idatt2105.gr2.backend.service.JwtService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
    private val jwtService: JwtService
) {
    private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        return try {
            val user: User = authenticationService.signup(request.name, request.email, request.password)
            val jwtToken = jwtService.generateToken(user)
            ResponseEntity.ok(UserResponse(user.userId, user.name, user.email, jwtToken, jwtService.getExpirationTime()))
        } catch (e: IllegalArgumentException) {
            logger.error("Error creating user: ${e.message}")
            ResponseEntity.badRequest().body(UserResponse(0, "", "", "", 0, e.message, 400))
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<UserResponse> {
        return try {
            val user: User = authenticationService.authenticate(request.email, request.password)
            val jwtToken = jwtService.generateToken(user)

            logger.info("Generated token for user ${user.email}")
            ResponseEntity.ok(UserResponse(user.userId, user.name, user.email, jwtToken, jwtService.getExpirationTime()))
        } catch (e: IllegalArgumentException) {
            logger.error("Error logging in user ${request.email}: ${e.message}")
            ResponseEntity.badRequest().body(UserResponse(0, "", "", "", 0, "Invalid email or password", 401))
        } catch (e: Exception) {
            logger.error("Unexpected error during login for ${request.email}: ${e.message}", e)
            ResponseEntity.internalServerError().body(UserResponse(0, "", "", "", 0, "An internal error occurred", 500))
        }
    }
}