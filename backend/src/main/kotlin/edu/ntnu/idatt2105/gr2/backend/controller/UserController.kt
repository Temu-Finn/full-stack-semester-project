package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.ChangePasswordRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UpdateEmailRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UpdateNameRequest
import edu.ntnu.idatt2105.gr2.backend.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User profile")
class UserController(
    private val userService: UserService,
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @PutMapping("/update-email")
    @Operation(summary = "Update user email", description = "Updates the email of the currently authenticated user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Email updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "409", description = "Email already in use"),
        ],
    )
    fun updateEmail(
        @Valid @RequestBody request: UpdateEmailRequest,
    ): ResponseEntity<Void> {
        logger.info("Request to update user email")
        userService.updateEmail(request)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/update-name")
    @Operation(summary = "Update user name", description = "Updates the name of the currently authenticated user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Name updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
        ],
    )
    fun updateName(
        @Valid @RequestBody request: UpdateNameRequest,
    ): ResponseEntity<Void> {
        logger.info("Request to update user name")
        userService.updateName(request)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/update-password")
    @Operation(summary = "Change user password", description = "Allows authenticated users to change their password")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Password updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input or current password"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
        ],
    )
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest,
    ): ResponseEntity<Void> {
        logger.info("Password change requested by current user")
        userService.changePassword(request)
        return ResponseEntity.ok().build()
    }
}
