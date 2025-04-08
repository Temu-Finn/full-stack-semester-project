package edu.ntnu.idatt2105.gr2.backend.dto

data class SearchItemRequest(
    val searchText: String? = null,
    val categoryId: Int? = null,
    val county: String? = null,
    val municipality: String? = null,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val maxDistance: Double? = null // in kilometers
) 