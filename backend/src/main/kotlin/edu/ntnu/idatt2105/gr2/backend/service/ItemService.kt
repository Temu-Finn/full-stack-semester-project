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

    fun getItemsByCategoryId(id: Long): List<Item> {
        return itemRepository.findAllByCategoryId(id)
    }

    fun deleteItemById(id: Long): Boolean {
        return itemRepository.deleteById(id)
    }

    private fun validateItem(item: Item){
        require(item.owner > 0) {"Item must be assigned an owner"} // Might not be necessary
        require(item.category > 0) {"Item must be assigned a category"} // Might not be necessary
        require(item.name.isNotEmpty()) {"Item must be assigned a name"}
        require(item.price > 0) {"Item must be assigned a price"}
    }
}