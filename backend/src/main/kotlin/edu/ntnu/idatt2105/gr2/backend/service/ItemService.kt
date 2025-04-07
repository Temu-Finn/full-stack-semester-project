package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.CreateItemRequest
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.dto.toItem
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val userContextService: UserContextService
) {
    private val logger = LoggerFactory.getLogger(ItemService::class.java)

    fun createItem(item: CreateItemRequest): Item {
        return try {
            itemRepository.create(item.toItem(userContextService.getCurrentUserId()))
        } catch (ex: Exception) {
            throw IllegalStateException("Failed to create item", ex)
        }
    }

    fun getItemById(id: Int): Item? {
        return itemRepository.getItemById(id)
    }

    fun deleteItem(itemId: Int): Boolean {
        val item = itemRepository.getItemById(itemId)
            ?: return false // 404 not found

        val currentUserId = userContextService.getCurrentUserId()

        if (item.sellerId != currentUserId) {
            logger.warn("User $currentUserId is not allowed to delete item ${item.id} owned by ${item.sellerId}")
            throw IllegalAccessException("You do not have permission to delete this item.")
        }

        return itemRepository.deleteById(itemId)
    }

    fun deleteItemById(id: Int): Boolean {
        return itemRepository.deleteById(id)
    }

    fun getItemsByCategoryId(categoryId: Int): List<Item> {
        return itemRepository.findAllByCategoryId(categoryId)
    }

    fun getAllByOwner(): List<Item> {
        val userId = userContextService.getCurrentUserId()
        logger.info("Fetching all items owned by user $userId")
        return itemRepository.findAllByOwner(userId)
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