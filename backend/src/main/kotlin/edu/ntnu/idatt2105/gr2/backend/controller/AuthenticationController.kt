package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.CreateUserRequest
import edu.ntnu.idatt2105.gr2.backend.dto.LoginRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UserResponse
import edu.ntnu.idatt2105.gr2.backend.model.User
import edu.ntnu.idatt2105.gr2.backend.service.AuthenticationService
import edu.ntnu.idatt2105.gr2.backend.service.JwtService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {
    private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    @PostMapping("/signup")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and returns a JWT token"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User successfully created"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "409", description = "User already exists")
        ]
    )
    fun signup(
        @Parameter(description = "User registration details", required = true)
        @Valid @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        logger.info("Attempting to create new user with email: ${request.email}")

        val user = authenticationService.signup(request.name, request.email, request.password)

        logger.info("Successfully created user with email: ${user.email}")
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user credentials and returns a JWT token"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "401", description = "Invalid credentials")
        ]
    )
    fun login(
        @Parameter(description = "User login credentials", required = true)
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<UserResponse> {
        logger.info("Login attempt for user with email: ${request.email}")
        
        val user = authenticationService.authenticate(request.email, request.password)

        logger.info("Successfully authenticated user with email: ${user.email}")
        return ResponseEntity.ok(user)
    }
}