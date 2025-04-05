package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService (
    private val itemRepository: ItemRepository,
) {
    fun getRecommendedItems(): List<ItemCard> {
        val testItem = ItemCard(
            itemId = 1,
            title = "Test Item",
            price = 100.0,
            municipality = "Test Municipality",
            imageUrl = "https://example.com/test.jpg"
        )
        return listOf(testItem)
    }
}