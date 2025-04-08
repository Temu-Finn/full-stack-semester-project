package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.service.VippsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/vipps")
@Tag(name = "Vipps", description = "Vipps test payment API")
class VippsController(
    private val vippsTestService: VippsService
) {

    private val logger = LoggerFactory.getLogger(VippsController::class.java)

    @PostMapping("/test-payment")
    @Operation(
        summary = "Initiate Vipps test payment",
        description = "Initiates a payment in the Vipps test environment using test sales unit credentials"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Payment initiated successfully"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun initiateTestPayment(): ResponseEntity<Map<*, *>> {
        logger.info("Initiating Vipps test payment")
        val response = vippsTestService.initiatePayment()
        logger.info("Vipps test payment initiated: $response")
        return ResponseEntity.ok(response)
    }
}
