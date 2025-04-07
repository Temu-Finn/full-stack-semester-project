package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val userContextService: UserContextService
) {
    private val logger = LoggerFactory.getLogger(ItemService::class.java)

    fun createItem(item: Item): Item {
        return try {
            itemRepository.create(item)
        } catch (ex: Exception) {
            throw IllegalStateException("Failed to create item", ex)
        }
    }

    fun getItemById(id: Long): Item? {
        return itemRepository.getItemById(id)
    }

    fun deleteItemById(id: Long): Boolean {
        return itemRepository.deleteById(id)
    }

    fun getItemsByCategoryId(categoryId: Long): List<Item> {
        return itemRepository.findAllByCategoryId(categoryId)
    }

    fun getAllItems(): List<Item> {
        return itemRepository.getAll()
    }

    fun deleteAllItems() {
        itemRepository.deleteAll()
    }

    fun getRecommendedItems(): List<ItemCard> {
        val userId = userContextService.getCurrentUserId()
        logger.info("Recommending items using userId $userId")
        return itemRepository.findRecommendedItems(userId)
    }
}