package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.dto.toItem
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import edu.ntnu.idatt2105.gr2.backend.exception.ItemNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val userContextService: UserContextService,
    private val imageService: ImageService,
) {
    private val logger = LoggerFactory.getLogger(ItemService::class.java)

    @Transactional
    fun createItem(itemRequest: CreateItemRequest): ItemResponse {
        logger.info("Creating new item: ${itemRequest.title}")
        val item = itemRepository.create(itemRequest.toItem(userContextService.getCurrentUserId()))
        val images = itemRequest.images.map { image ->
            imageService.upload(item.id, image)
        }

        var primaryImageId: Int? = null
        if (images.isNotEmpty()) {
            primaryImageId = images.first().id
            itemRepository.setPrimaryImage(item.id, primaryImageId)
        }
        
        return ItemResponse(
            id = item.id,
            title = item.title,
            description = item.description,
            price = item.price,
            purchasePrice = item.purchasePrice,
            location = item.location,
            images = images,
            status = item.status,
            sellerId = item.sellerId,
            categoryId = item.categoryId,
            postalCode = item.postalCode,
            buyerId = item.buyerId,
            allowVippsBuy = item.allowVippsBuy,
            primaryImageId = primaryImageId,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt,
            municipality = item.municipality
        )
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

    fun getAllByOwner(): List<Item> {
        val userId = userContextService.getCurrentUserId()
        logger.info("Fetching all items owned by user $userId")
        return itemRepository.findAllBySellerId(userId)
    }

    fun getRecommendedItems(): List<ItemCard> {
        logger.info("Fetching recommended items")
        return itemRepository.findRecommendedItems()
    }

    fun searchItems(request: SearchItemRequest): List<ItemCard> {
        logger.info("Searching items with request: $request")
        return itemRepository.searchItems(request)
    }
}