package edu.ntnu.idatt2105.gr2.backend.dto

import org.springframework.data.domain.Page

data class SearchRequest(
    val searchText: String? = null,
    val categoryId: Int? = null,
    val county: String? = null,
    val municipality: String? = null,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val maxDistanceKm: Double? = null // Maximum distance in kilometers
)

data class SearchResponse(
    val counties: List<CountyResponse>,
    val items: Page<ItemCard>
)