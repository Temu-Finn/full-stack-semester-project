package edu.ntnu.idatt2105.gr2.backend.service

import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class VippsService {

    private val baseUrl = "https://apitest.vipps.no"
    private val clientId = "835cd6a8-33fa-4677-9796-fdf5898fdd7e"
    private val clientSecret = "r8i8Q~Lnf5K4tF.i9RHpJqR~BAYLr9IrXE3wya~-"
    private val subscriptionKey = "0e36824f633a4063a8525c3b476aa922"
    private val merchantSerialNumber = "367319"

    private val restTemplate = RestTemplate()

    fun getAccessToken(): String? {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("client_id", clientId)
            set("client_secret", clientSecret)
            set("Ocp-Apim-Subscription-Key", subscriptionKey)
        }
        val request = HttpEntity(null, headers)

        val response = restTemplate.exchange(
            "$baseUrl/accesstoken/get",
            HttpMethod.POST,
            request,
            Map::class.java
        )

        return response.body?.get("access_token") as? String
    }

    fun initiatePayment(): Map<*, *>? {
        val accessToken = getAccessToken() ?: throw RuntimeException("Failed to get access token")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(accessToken)
            set("Ocp-Apim-Subscription-Key", subscriptionKey)
            set("X-Request-Id", UUID.randomUUID().toString())
            set("Idempotency-Key", UUID.randomUUID().toString())
            set("Merchant-Serial-Number", merchantSerialNumber)
        }

        val reference = UUID.randomUUID().toString()

        val requestBody = mapOf(
            "amount" to mapOf(
                "currency" to "NOK",
                "value" to 1000
            ),
            "reference" to UUID.randomUUID().toString(),
            "returnUrl" to "https://example.com/redirect",
            "userFlow" to "WEB_REDIRECT",
            "paymentMethod" to mapOf(
                "type" to "WALLET"
            )
        )



        val request = HttpEntity(requestBody, headers)

        val response = restTemplate.exchange(
            "$baseUrl/epayment/v1/payments",
            HttpMethod.POST,
            request,
            Map::class.java
        )

        return response.body
    }




}