package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.CountyResponse
import edu.ntnu.idatt2105.gr2.backend.dto.SearchRequest
import edu.ntnu.idatt2105.gr2.backend.repository.AreaRepository
import org.springframework.stereotype.Service

@Service
class AreaService (
    private val areaRepository: AreaRepository,
) {
    fun populateCounties(request: SearchRequest): List<CountyResponse> {
        val counties = areaRepository.getCounties(request)

    }

}