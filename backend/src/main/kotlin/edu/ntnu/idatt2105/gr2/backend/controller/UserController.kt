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
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Controller for handling user-related requests. It includes endpoints for
 * updating user email, name, and password.
 *
 * @constructor              Creates a new instance of UserController.
 * @param userService       The service for handling user-related operations.
 */

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "User profile")
class UserController(
    private val userService: UserService,
) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    /**
     * Handles client requests to update user email. It delegates the request to the
     * service layer and returns response status from service layer
     *
     * @param request     UpdateEmailRequest                The user email update details.
     * @return            ResponseEntity<Void>              contains the status code.
     */

    @PutMapping("/update-email")
    @Operation(summary = "Update user email",
        description = "Updates the email of the currently authenticated user")
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

    /**
     * Handles client requests to update user name. It delegates the request to the
     * service layer and returns response status from service layer
     *
     * @param request     UpdateNameRequest                The user name update details.
     * @return            ResponseEntity<Void>             Contains the status code.
     */

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

    /**
     * Handles client requests to change user password. It delegates the request to the
     * service layer and returns response status from service layer
     *
     * @param request     ChangePasswordRequest            The user password change details.
     * @return            ResponseEntity<Void>             Contains the status code.
     */

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
