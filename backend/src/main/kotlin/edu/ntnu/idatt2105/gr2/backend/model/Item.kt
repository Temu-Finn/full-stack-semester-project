package edu.ntnu.idatt2105.gr2.backend.model

import java.time.LocalDateTime

sealed class ItemStatus {
    object Available : ItemStatus()
    object Reserved : ItemStatus()
    object Sold : ItemStatus()
    object Archived : ItemStatus()

    override fun toString(): String = when (this) {
        is Available -> "available"
        is Reserved -> "reserved"
        is Sold -> "sold"
        is Archived -> "archived"
    }

    companion object {
        fun fromString(value: String): ItemStatus = when (value) {
            "available" -> Available
            "reserved" -> Reserved
            "sold" -> Sold
            "archived" -> Archived
            else -> throw IllegalArgumentException("Invalid status: $value")
        }
    }
}

data class Item(
    val id: Int = -1,
    val sellerId: Int,
    val categoryId: Int,
    val postalCode: String,
    val title: String,
    val description: String,
    val price: Double,
    val purchasePrice: Double?,
    val buyerId: Int?,
    val location: Pair<Double, Double>?, // Latitude, Longitude
    val allowVippsBuy: Boolean = false,
    val primaryImageId: Int?,
    val status: ItemStatus = ItemStatus.Available,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    init {
        require(sellerId > 0) { "Item must be assigned a seller" }
        require(categoryId > 0) { "Item must be assigned a category" }
        require(postalCode.isNotBlank()) { "Postal code must be specified" }
        require(title.isNotBlank()) { "Title must be specified" }
        require(description.isNotBlank()) { "Description must be specified" }
        require(price >= 0) { "Price must be zero or positive" }
        require(purchasePrice == null || purchasePrice >= 0) { "Purchase price must be zero or positive" }
        require(location == null || (location.first in -90.0..90.0 && location.second in -180.0..180.0)) {
            "Location coordinates must be valid (latitude: -90 to 90, longitude: -180 to 180)"
        }
    }

    /**
     * Converts the item to a basic card view
     * Note: municipality and imageBase64 will be populated by the repository layer
     */
    fun toCard(): ItemCard = ItemCard(
        id = id,
        title = title,
        price = price,
        municipality = "", // Will be populated by repository
        imageBase64 = "", // Will be populated by repository
        location = location?.let { Location(it.first, it.second) },
        status = status,
        updatedAt = updatedAt
    )

    /**
     * Converts the item to a detailed response
     * Note: municipality and images will be populated by the repository layer
     */
    fun toResponse(): ItemResponse = ItemResponse(
        id = id,
        sellerId = sellerId,
        categoryId = categoryId,
        postalCode = postalCode,
        title = title,
        description = description,
        price = price,
        purchasePrice = purchasePrice,
        buyerId = buyerId,
        location = location?.let { Location(it.first, it.second) },
        allowVippsBuy = allowVippsBuy,
        primaryImageId = primaryImageId,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        municipality = "", // Will be populated by repository
        images = emptyList() // Will be populated by repository
    )
}
