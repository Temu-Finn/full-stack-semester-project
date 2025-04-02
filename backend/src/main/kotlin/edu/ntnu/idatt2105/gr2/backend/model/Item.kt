package edu.ntnu.idatt2105.gr2.backend.model

import java.time.LocalDateTime

data class Item (
    val id: Long = -1,
    val sellerId: Long,
    val categoryId: Long,
    val postalCode: String,
    val title: String,
    val description: String,
    val price: Double,
    val purchasePrice: Double?,
    val buyerId: Long?,
    val location: Pair<Double, Double>?, // Latitude, Longitude
    val allowVippsBuy: Boolean = false,
    val primaryImageId: Long?,
    val status: String = "available",
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)