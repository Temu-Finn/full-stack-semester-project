package edu.ntnu.idatt2105.gr2.backend.model

import edu.ntnu.idatt2105.gr2.backend.dto.Location
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
    val location: Location?, // Latitude, Longitude
    val allowVippsBuy: Boolean = false,
    val primaryImageId: Int?,
    val status: ItemStatus = ItemStatus.Available,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val municipality: String = "Unknown",
) {
    init {
        require(sellerId > 0) { "Item must be assigned a seller" }
        require(categoryId > 0) { "Item must be assigned a category" }
        require(postalCode.isNotBlank()) { "Postal code must be specified" }
        require(title.isNotBlank()) { "Title must be specified" }
        require(description.isNotBlank()) { "Description must be specified" }
        require(price >= 0) { "Price must be zero or positive" }
        require(purchasePrice == null || purchasePrice >= 0) { "Purchase price must be zero or positive" }
        require(location == null || (location.latitude in -90.0..90.0 && location.longitude in -180.0..180.0)) {
            "Location coordinates must be valid (latitude: -90 to 90, longitude: -180 to 180)"
        }
    }
}
