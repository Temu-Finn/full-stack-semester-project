package edu.ntnu.idatt2105.gr2.backend.service

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

/**
 * Service class for handling communication with the Vipps ePayment API.
 *
 * This service includes methods for:
 * - Obtaining an access token
 * - Initiating a payment
 * - Retrieving the status/details of an existing payment
 *
 * All calls are directed towards the Vipps test environment.
 */
@Service
class VippsService {
    private val dotenv = Dotenv.load()

    private val baseUrl = "https://apitest.vipps.no"
    private val clientId = dotenv["VIPPS_CLIENT_ID"]
    private val clientSecret = dotenv["VIPPS_CLIENT_SECRET"]
    private val subscriptionKey = dotenv["VIPPS_SUBSCRIPTION_KEY"]
    private val merchantSerialNumber = dotenv["VIPPS_MERCHANT_SERIAL_NUMBER"]

    private val restTemplate = RestTemplate()

    /**
     * Retrieves an access token from the Vipps access token endpoint.
     *
     * This token is required to authenticate subsequent API calls to Vipps.
     *
     * @return The access token as a string, or `null` if the request failed.
     */
    fun getAccessToken(): String? {
        val headers =
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("client_id", clientId)
                set("client_secret", clientSecret)
                set("Ocp-Apim-Subscription-Key", subscriptionKey)
            }
        val request = HttpEntity(null, headers)

        val response =
            restTemplate.exchange(
                "$baseUrl/accesstoken/get",
                HttpMethod.POST,
                request,
                Map::class.java,
            )

        return response.body?.get("access_token") as? String
    }

    /**
     * Initiates a Vipps payment with the given price.
     *
     * This method builds and sends a `POST` request to the Vipps payment endpoint.
     *
     * @param price The total payment amount in øre (e.g., 49900 for 499 NOK).
     * @return A map containing the response body from Vipps, such as redirect URL and pspReference.
     * @throws RuntimeException if the access token cannot be retrieved.
     */
    fun initiatePayment(price: Double): Map<*, *>? {
        val accessToken = getAccessToken() ?: throw RuntimeException("Failed to get access token")

        val headers =
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                setBearerAuth(accessToken)
                set("Ocp-Apim-Subscription-Key", subscriptionKey)
                set("X-Request-Id", UUID.randomUUID().toString())
                set("Idempotency-Key", UUID.randomUUID().toString())
                set("Merchant-Serial-Number", merchantSerialNumber)
            }

        val reference = UUID.randomUUID().toString()

        val requestBody =
            mapOf(
                "amount" to
                    mapOf(
                        "currency" to "NOK",
                        "value" to price * 100,
                    ),
                "reference" to reference,
                "returnUrl" to "http://localhost:5173/vipps/processing?ref=$reference",
                "userFlow" to "WEB_REDIRECT",
                "paymentMethod" to
                    mapOf(
                        "type" to "WALLET",
                    ),
            )

        val request = HttpEntity(requestBody, headers)

        val response =
            restTemplate.exchange(
                "$baseUrl/epayment/v1/payments",
                HttpMethod.POST,
                request,
                Map::class.java,
            )

        return response.body
    }

    /**
     * Retrieves the details of an existing Vipps payment by reference.
     *
     * Sends a `GET` request to the Vipps API to check the status and metadata of the payment.
     *
     * @param reference The unique reference identifier used to initiate the payment.
     * @return A map containing the payment details returned from Vipps.
     * @throws RuntimeException if the access token cannot be retrieved.
     */
    fun getPayment(reference: String): Map<*, *>? {
        val accessToken = getAccessToken() ?: throw RuntimeException("Failed to get access token")

        val headers =
            HttpHeaders().apply {
                setBearerAuth(accessToken)
                set("Ocp-Apim-Subscription-Key", subscriptionKey)
                set("Merchant-Serial-Number", merchantSerialNumber)
                set("X-Request-Id", UUID.randomUUID().toString())
            }

        val entity = HttpEntity<Void>(headers)

        val response =
            restTemplate.exchange(
                "$baseUrl/epayment/v1/payments/$reference",
                HttpMethod.GET,
                entity,
                Map::class.java,
            )

        return response.body
    }
}
