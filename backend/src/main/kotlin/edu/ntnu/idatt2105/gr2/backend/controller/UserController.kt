package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.UpdateEmailRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UpdateNameRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UserProfile
import edu.ntnu.idatt2105.gr2.backend.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User profile")
class UserController(
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Fetches profile information of the logged-in user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User profile fetched"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    fun getCurrentUserProfile(): ResponseEntity<UserProfile> {
        logger.info("Fetching current user profile")
        return ResponseEntity.ok(userService.getCurrentUserProfile())
    }

    @PutMapping("me/updateEmail")
    @Operation(summary = "Update user email", description = "Updates the email of the currently authenticated user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Email updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "409", description = "Email already in use")
        ]
    )
    fun updateEmail(@Valid @RequestBody request: UpdateEmailRequest): ResponseEntity<Void> {
        logger.info("Request to update user email")
        userService.updateEmail(request)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/me/updateName")
    @Operation(summary = "Update user name", description = "Updates the name of the currently authenticated user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Name updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "401", description = "Unauthorized")
        ]
    )
    fun updateName(@Valid @RequestBody request: UpdateNameRequest): ResponseEntity<Void> {
        logger.info("Request to update user name")
        userService.updateName(request)
        return ResponseEntity.ok().build()
    }
}