package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.CreateItemRequest
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.dto.toItem
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import edu.ntnu.idatt2105.gr2.backend.exception.ItemNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val userContextService: UserContextService
) {
    private val logger = LoggerFactory.getLogger(ItemService::class.java)

    @Transactional
    fun createItem(item: CreateItemRequest): Item {
        logger.info("Creating new item: ${item.title}")
        return itemRepository.create(item.toItem(userContextService.getCurrentUserId()))
    }

    fun getItemById(id: Int): Item {
        logger.info("Fetching item with ID: $id")
        return itemRepository.getItemById(id)
            ?: throw ItemNotFoundException("Item with ID $id not found")
    }

    @Transactional
    fun deleteItem(itemId: Int): Boolean {
        val item = getItemById(itemId)
        val currentUserId = userContextService.getCurrentUserId()

        if (item.sellerId != currentUserId) {
            logger.warn("User $currentUserId is not allowed to delete item ${item.id} owned by ${item.sellerId}")
            throw IllegalAccessException("You do not have permission to delete this item.")
        }

        return itemRepository.deleteById(itemId)
    }

    fun getItemsByCategoryId(categoryId: Int): List<Item> {
        logger.info("Fetching items for category ID: $categoryId")
        return itemRepository.findAllByCategoryId(categoryId)
    }

    fun getAllByOwner(): List<Item> {
        val userId = userContextService.getCurrentUserId()
        logger.info("Fetching all items owned by user $userId")
        return itemRepository.findAllBySellerId(userId)
    }

    fun getRecommendedItems(): List<ItemCard> {
        logger.info("Fetching recommended items")
        return itemRepository.findRecommendedItems()
    }

    fun findItemsNearby(latitude: Double, longitude: Double, maxDistance: Int): List<Item> {
        logger.info("Finding items near coordinates: ($latitude, $longitude) within $maxDistance meters")
        return itemRepository.findItemsNearby(latitude, longitude, maxDistance)
    }
}