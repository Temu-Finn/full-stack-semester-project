package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.CountyResponse
import edu.ntnu.idatt2105.gr2.backend.dto.MunicipalityResponse
import edu.ntnu.idatt2105.gr2.backend.dto.SearchRequest
import edu.ntnu.idatt2105.gr2.backend.repository.AreaRepository
import org.springframework.stereotype.Service

@Service
class AreaService (
    private val areaRepository: AreaRepository,
) {
    fun populateCounties(request: SearchRequest): List<CountyResponse> {
        val counties = areaRepository.getCounties(request.copy(county = null, municipality = null, city = null))

        data class MutableCountyResponse(
            val name: String,
            var count: Int,
            val municipalities: MutableList<MunicipalityResponse>
        )

        val countyMap = mutableMapOf<String, MutableCountyResponse>()
        counties.forEach {
            val countyName = it.county
            val municipalityName = it.municipality
            val itemCount = it.count

            if (countyMap.containsKey(countyName)) {
                val existingCounty = countyMap[countyName]!!
                existingCounty.municipalities.add(
                    MunicipalityResponse(
                        name = municipalityName,
                        count = itemCount
                    )
                )
                existingCounty.count += itemCount
            } else {
                countyMap[countyName] = MutableCountyResponse(
                    name = countyName,
                    count = itemCount,
                    municipalities = mutableListOf(
                        MunicipalityResponse(
                            name = municipalityName,
                            count = itemCount
                        )
                    )
                )
            }
        }

        return countyMap.values.map {
            CountyResponse(
                name = it.name,
                count = it.count,
                municipalities = it.municipalities
            )
        }
    }

}