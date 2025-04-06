package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService (
    private val itemRepository: ItemRepository,
) {
    fun getRecommendedItems(): List<ItemCard> {
        return itemRepository.findRecommendedItems()
    }
}