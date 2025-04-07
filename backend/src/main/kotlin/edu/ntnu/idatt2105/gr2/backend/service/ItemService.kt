package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.controller.AuthenticationController
import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ItemService (
    private val itemRepository: ItemRepository,
    private val userContextService: UserContextService
) {
    private val logger = LoggerFactory.getLogger(ItemService::class.java)

    fun getRecommendedItems(): List<ItemCard> {
        val userId = userContextService.getCurrentUserId()
        logger.info("Recommending items using userId $userId")
        return itemRepository.findRecommendedItems(userId)
    }
}