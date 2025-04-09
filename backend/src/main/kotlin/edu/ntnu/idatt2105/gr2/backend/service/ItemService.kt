package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.dto.toItem
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import edu.ntnu.idatt2105.gr2.backend.exception.ItemNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val userContextService: UserContextService,
    private val imageService: ImageService,
    private val categoryService: CategoryService,
) {
    private val logger = LoggerFactory.getLogger(ItemService::class.java)

    @Transactional
    fun createItem(itemRequest: CreateItemRequest, images: List<MultipartFile>): CompleteItem {
        logger.info("Creating new item: ${itemRequest.title}")
        val item = itemRepository.create(itemRequest.toItem(userContextService.getCurrentUserId()))
        val imagesResponse = images.map { image ->
            imageService.upload(item.id, CreateImageRequest(image))
        }

        var primaryImageId: Int? = null
        if (imagesResponse.isNotEmpty()) {
            primaryImageId = imagesResponse.first().id
            itemRepository.setPrimaryImage(item.id, primaryImageId)
        }

        return CompleteItem(
            id = item.id,
            title = item.title,
            description = item.description,
            price = item.price,
            purchasePrice = item.purchasePrice,
            location = item.location,
            images = imagesResponse,
            status = item.status.toString(),
            sellerId = item.sellerId,
            category = categoryService.getCategory(item.categoryId),
            postalCode = item.postalCode,
            buyerId = item.buyerId,
            allowVippsBuy = item.allowVippsBuy,
            primaryImageId = primaryImageId,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt,
            municipality = item.municipality
        )
    }

    fun getItemById(id: Int): CompleteItem {
        logger.info("Fetching item with ID: $id")
        val item = itemRepository.getItemById(id)
            ?: throw ItemNotFoundException("Item with ID $id not found")
        return item.toResponse(withImages = true)
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

    fun getRecommendedItems(): List<ItemCard> {
        logger.info("Fetching recommended items")
        return itemRepository.findRecommendedItems().map { it.toCard() }
    }

    fun searchItems(request: SearchItemRequest, pageable: Pageable): Page<ItemCard> {
        logger.info("Searching items with request: $request and pageable: $pageable")
        val itemPage = itemRepository.searchItems(request, pageable)
        val itemCards = itemPage.content.map { it.toCard() }
        return PageImpl(itemCards, pageable, itemPage.totalElements)
    }

    fun getItemsOfUser(userId: Int): List<ItemCard> {
        val isOwnUser = userId == userContextService.getCurrentUserId()
        logger.info("Fetching items for user ID: $userId")
        return itemRepository.findAllBySellerId(userId, isOwnUser).map {  it.toCard() }
    }

    fun Item.toResponse(withImages: Boolean = false): CompleteItem {
        return CompleteItem(
            id = this.id,
            sellerId = this.sellerId,
            category = categoryService.getCategory(this.categoryId),
            title = this.title,
            description = this.description,
            price = this.price,
            purchasePrice = this.purchasePrice,
            buyerId = this.buyerId,
            location = this.location,
            allowVippsBuy = this.allowVippsBuy,
            primaryImageId = this.primaryImageId,
            status = this.status.toString(),
            images = if (withImages)
                imageService.getImagesByItemId(this.id)
            else
                emptyList(),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            municipality = this.municipality,
            postalCode = this.postalCode
        )
    }

    fun Item.toCard(): ItemCard {
        return ItemCard(
            id = id,
            title = title,
            price = price,
            municipality = municipality,
            image = primaryImageId?.let { imageService.getImageById(primaryImageId)},
            location = location,
            status = status.toString(),
            updatedAt = updatedAt
        )
    }
}
