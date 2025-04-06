package edu.ntnu.idatt2105.gr2.backend.dto

data class ItemCard (
    val itemId: Int,
    val title: String,
    val price: Double,
    val municipality: String,
    val imageBase64: String?,
)

data class RecommendedItemsResponse(
    val items: List<ItemCard>,
)