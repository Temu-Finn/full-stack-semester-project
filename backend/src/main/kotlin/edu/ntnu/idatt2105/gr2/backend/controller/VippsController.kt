package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.VippsPaymentRequest
import edu.ntnu.idatt2105.gr2.backend.service.VippsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for handling Vipps payment operations.
 *
 * Provides endpoints for initiating and retrieving payments using the Vipps ePayment API.
 *
 * All requests made through this controller use the test sales unit credentials.
 */
@RestController
@RequestMapping("/api/vipps")
@Tag(name = "Vipps", description = "Vipps test payment API")
class VippsController(
    private val vippsService: VippsService,
) {
    private val logger = LoggerFactory.getLogger(VippsController::class.java)

    /**
     * Initiates a Vipps payment with an amount given from the frontend
     *
     * This endpoint sends a `POST` request to Vipps to start a new payment using
     * the test environment and returns the response with payment details including redirect URL.
     *
     * @return A [ResponseEntity] containing the response from Vipps ePayment API.
     */
    @PostMapping("/payment")
    @Operation(
        summary = "Initiate Vipps payment",
        description = "Initiates a payment in the Vipps test environment using test sales unit credentials",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment initiated successfully"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun initiatePayment(
        @RequestBody request: VippsPaymentRequest,
    ): ResponseEntity<Map<*, *>> {
        logger.info("Initiating Vipps test payment")
        val response = vippsService.initiatePayment(request.price)
        logger.info("Vipps test payment initiated: $response")
        return ResponseEntity.ok(response)
    }

    /**
     * Retrieves the status and details of a Vipps payment using its reference ID.
     *
     * This endpoint sends a `GET` request to Vipps to look up a previously created payment
     * based on its unique reference.
     *
     * @param reference The unique identifier used to track the payment.
     * @return A [ResponseEntity] containing the payment status and metadata from Vipps.
     */
    @GetMapping("/payment/{reference}")
    @Operation(
        summary = "Get Vipps payment",
        description = "Gets a Vipps payment for the given reference",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment retrieved"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun getPayment(
        @PathVariable reference: String,
    ): ResponseEntity<Map<*, *>> {
        val payment = vippsService.getPayment(reference)
        return ResponseEntity.ok(payment)
    }
}
