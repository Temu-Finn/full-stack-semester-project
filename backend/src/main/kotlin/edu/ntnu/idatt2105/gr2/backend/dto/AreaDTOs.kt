package edu.ntnu.idatt2105.gr2.backend.dto

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
