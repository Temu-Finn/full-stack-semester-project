package edu.ntnu.idatt2105.gr2.backend.dto

data class SearchItemRequest(
    val searchText: String? = null,
    val categoryId: Int? = null,
    val countyId: Int? = null,
    val municipalityId: Int? = null,
    val cityId: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val maxDistance: Double? = null // in kilometers
) 