package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.dto.toItem
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import edu.ntnu.idatt2105.gr2.backend.exception.ItemNotFoundException
import edu.ntnu.idatt2105.gr2.backend.model.ItemStatus
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val userContextService: UserContextService,
    private val imageService: ImageService,
    private val categoryService: CategoryService,
    private val areaService: AreaService,
    private val userRepository: UserRepository,
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
        return item.toResponse().withImages()
    }

    fun getItemCardById(id: Int): ItemCard {
        logger.info("Fetching item card with ID: $id")
        val item = itemRepository.getItemById(id)
            ?: throw ItemNotFoundException("Item with ID $id not found")
        return item.toResponse().toCard()
    }

    @Transactional
    fun deleteItem(itemId: Int): Boolean {
        val item = getItemById(itemId)
        val currentUserId = userContextService.getCurrentUserId()

        if (item.sellerId != currentUserId && !userRepository.isAdmin(currentUserId)) {
            logger.warn("User $currentUserId is not allowed to delete item ${item.id} owned by ${item.sellerId}")
            throw IllegalAccessException("You do not have permission to delete this item.")
        }

        return itemRepository.deleteById(itemId)
    }

    fun getRecommendedItems(): List<ItemCard> {
        logger.info("Fetching recommended items")
        return itemRepository.findRecommendedItems().map { it.toResponse().toCard() }
    }

    fun searchItems(request: SearchRequest, pageable: Pageable): SearchResponse {
        logger.info("Searching items with request: $request and pageable: $pageable")
        val itemPage = itemRepository.searchItems(request, pageable)
        val counties = areaService.populateCounties(request)
        val itemCards = itemPage.content.map { it.toResponse().toCard() }
        return SearchResponse(counties, PageImpl(itemCards, pageable, itemPage.totalElements))
    }

    fun getItemsOfUser(userId: Int): List<ItemCard> {
        val isOwnUser = userId == userContextService.getCurrentUserId()
        logger.info("Fetching items for user ID: $userId")
        val items = itemRepository.findAllBySellerId(userId, isOwnUser).map {  it.toResponse().toCard() }
        if (!isOwnUser) {
            return items
        }

        val boughtItems = itemRepository.findAllBought(userId).map { it.toResponse().toCard().copy(status = "bought") }
        val reservedItems = itemRepository.findAllReserved(userId).map {  it.toResponse().toCard().copy(status = "reserved_by_user") }

        return items.plus(boughtItems).plus(reservedItems)
    }

    fun getFavoriteItemsOfCurrentUser(): List<ItemCard> {
        val userId = userContextService.getCurrentUserId()
        logger.info("Fetching favourite items for user ID: $userId")
        return itemRepository.findFavoriteByUserId(userId).map { it.toResponse().toCard() }
    }

    fun Item.toResponse(): CompleteItem {
        return CompleteItem(
            id = id,
            sellerId = sellerId,
            category = categoryService.getCategory(categoryId),
            title = title,
            description = description,
            price = price,
            purchasePrice = purchasePrice,
            buyerId = buyerId,
            location = location,
            allowVippsBuy = allowVippsBuy,
            primaryImageId = primaryImageId,
            status = getResponseStatus(),
            images = emptyList(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            municipality = municipality,
            postalCode = postalCode
        )
    }

    fun Item.getResponseStatus(): String {
        if (buyerId == userContextService.getCurrentUserId()) {
            if (status == ItemStatus.Sold) {
                return "bought"
            } else if (status == ItemStatus.Reserved) {
                return "reserved_by_user"
            }
        }

        return status.toString()
    }

    fun CompleteItem.withImages(): CompleteItem {
        return this.copy(
            images = imageService.getImagesByItemId(this.id)
        )
    }

    fun CompleteItem.toCard(): ItemCard {
        return ItemCard(
            id = id,
            title = title,
            price = price,
            municipality = municipality,
            image = primaryImageId?.let { imageService.getImageById(primaryImageId)},
            location = location,
            status = status,
            updatedAt = updatedAt,
            allowVippsBuy = allowVippsBuy
        )
    }

    fun reserveItem(itemId: Int): CompleteItem {
        val userId = userContextService.getCurrentUserId()
        logger.info("Reserving item with ID: $itemId for user ID: $userId")
        itemRepository.reserveItem(itemId, userId)
        return itemRepository.getItemById(itemId)?.toResponse() ?: throw ItemNotFoundException("Item with ID $itemId not found")
    }
}
