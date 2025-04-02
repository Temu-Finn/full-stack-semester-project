package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService(private val itemRepository: ItemRepository) {

    fun createItem(item: Item): Item {
        validateItem(item)
        return itemRepository.create(item)
    }

    fun getItemsByCategoryId(categoryId: Long): List<Item> {
        return itemRepository.findAllByCategoryId(categoryId)
    }

    fun deleteItemById(id: Long): Boolean {
        return itemRepository.deleteById(id)
    }

    private fun validateItem(item: Item) {
        require(item.sellerId > 0) { "Item must be assigned a seller" }
        require(item.categoryId > 0) { "Item must be assigned a category" }
        require(item.postalCode.isNotEmpty()) { "Postal code must be specified" }
        require(item.title.isNotEmpty()) { "Title must be specified" }
        require(item.description.isNotEmpty()) { "Description must be specified" }
        require(item.price >= 0) { "Price must be zero or positive" }
        require(item.status in listOf("available", "reserved", "sold", "archived")) { "Invalid status" }
    }
}