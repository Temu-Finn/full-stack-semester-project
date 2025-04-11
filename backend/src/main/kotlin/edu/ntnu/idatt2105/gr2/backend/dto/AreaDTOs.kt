package edu.ntnu.idatt2105.gr2.backend.dto

/**
 * Data Transfer Objects (DTOs) for representing areas and their counties.
 * Used for transferring data between the backend and frontend
 */

interface Area {
    val name: String
    val count: Int
}

data class CountyResponse(
    override val name: String,
    override val count: Int,
    val municipalities: List<MunicipalityResponse>,
) : Area

data class MunicipalityResponse(
    override val name: String,
    override val count: Int,
) : Area
