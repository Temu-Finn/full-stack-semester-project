package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.CreateUserRequest
import edu.ntnu.idatt2105.gr2.backend.dto.LoginRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UserResponse
import edu.ntnu.idatt2105.gr2.backend.service.AuthenticationService
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

/**
 * Controller for setting up authentication-related endpoints receiving requests from client.
 * Controller includes user registration and login.
 *
 * @constructor                   Creates a new instance of AuthenticationController.
 * @param authenticationService   The service for handling authentication-related operations.
 */

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {

    private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    /**
     * Handles user registration requests from client. It delegates the request to the
     * service layer and returns created user details and user JWT token to the client
     * from service layer,  as well as a status code.
     *
     * @param request     CreateUserRequest                The user registration details.
     * @return            ResponseEntity UserResponse      contains the created user
     *                                                     details and a status code.
     */

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

    /**
     * Handles login requests from client and delegates the request to the service layer.
     * It returns the user details and a JWT token to the client, as well as a status code.
     *
     * @param request       LoginRequest                    The user login credentials.
     * @return              ResponseEntity UserResponse     containing the user details and
     *                                                      a status code.
     */

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user credentials and returns a JWT token"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "40|1", description = "Invalid credentials")
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