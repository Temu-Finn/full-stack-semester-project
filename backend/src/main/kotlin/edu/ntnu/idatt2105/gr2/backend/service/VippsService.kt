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
    private val merchantSerialNumber = ""

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
            "$baseUrl/access-token/get",
            HttpMethod.POST,
            request,
            Map::class.java
        )

        return response.body?.get("access_token") as? String
    }

    fun initiatePayment(): Map<*, *>? {
        val accesToken = getAccessToken() ?: throw RuntimeException("Failed to get access token!!")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(accesToken)
            set("Ocp-Apim-Subscription-Key", subscriptionKey)
            set("X-Request-ID", UUID.randomUUID().toString())
        }

        val orderId = UUID.randomUUID().toString()
        val requestBody = mapOf(
            "merchantInfo" to mapOf(
                "merchantSerialNumber" to merchantSerialNumber
            ),
            "transaction" to mapOf(
                "orderId" to orderId,
                "amount" to mapOf(
                    "currency" to "NOK",
                    "value" to 1000 // 1000 øre = 10 NOK
                ),
                "transactionText" to "Testbetaling med RestTemplate"
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