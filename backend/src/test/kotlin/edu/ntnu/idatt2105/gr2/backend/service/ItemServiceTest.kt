package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*


class ItemServiceTest {

    private lateinit var itemRepository: ItemRepository
    private lateinit var itemService: ItemService

    @BeforeEach
    fun setUp() {
        itemRepository = mock(ItemRepository::class.java)
        itemService = ItemService(itemRepository)
    }

    private fun createValidItem(): Item {
        return Item(
            sellerId = 1,
            categoryId = 2,
            postalCode = "7014",
            title = "Test item",
            description = "A valid item",
            price = 100.0,
            purchasePrice = null,
            buyerId = null,
            location = null,
            allowVippsBuy = true,
            primaryImageId = null,
            status = "available"
        )
    }

    @Nested
    @DisplayName("Positive tests create item")
    inner class CreateItemPositiveTest {
        @Test
        @DisplayName("should create item successfully")
        fun `should create item successfully`() {
            val item = createValidItem()
            val savedItem = item.copy(id = 1)

            `when`(itemRepository.create(item)).thenReturn(savedItem)

            val result = itemService.createItem(item)

            assertEquals(savedItem.id, result.id)
            verify(itemRepository, times(1)).create(item)
        }
    }

    @Nested
    @DisplayName("Positive tests get items by category")
    inner class GetItemsByCategoryTest {

        @Test
        @DisplayName("should return list of items for specific category")
        fun `should return list of items for category`() {
            val categoryId = 2L
            val items = listOf(
                createValidItem().copy(id = 1, categoryId = categoryId),
                createValidItem().copy(id = 2, categoryId = categoryId, title = "Another Item")
            )
            val itemWithDiffCategoryId = createValidItem().copy(id = 3, categoryId = 3L)

            `when`(itemRepository.findAllByCategoryId(categoryId)).thenReturn(items)

            val result = itemService.getItemsByCategoryId(categoryId)

            assertEquals(2, result.size)
            assertEquals("Test item", result[0].title)
            assertEquals("Another Item", result[1].title)
            verify(itemRepository, times(1)).findAllByCategoryId(categoryId)
        }
    }

    @Nested
    @DisplayName("Negative tests create item")
    inner class CreateItemNegativeTest {

        @Test
        @DisplayName("should fail to create item with invalid sellerId")
        fun `should fail to create item with invalid sellerId`() {
            val item = createValidItem().copy(sellerId = 0)

            val exception = assertThrows(IllegalArgumentException::class.java) {
                itemService.createItem(item)
            }

            assertEquals("Item must be assigned a seller", exception.message)
            verifyNoInteractions(itemRepository)
        }

        @Test
        @DisplayName("should fail to create item with invalid categoryId")
        fun `should fail to create item with invalid categoryId`() {
            val item = createValidItem().copy(categoryId = 0)

            val exception = assertThrows(IllegalArgumentException::class.java) {
                itemService.createItem(item)
            }

            assertEquals("Item must be assigned a category", exception.message)
            verifyNoInteractions(itemRepository)
        }

        @Test
        @DisplayName("should fail to create item with empty postal code")
        fun `should fail to create item with empty postal code`() {
            val item = createValidItem().copy(postalCode = "")

            val exception = assertThrows(IllegalArgumentException::class.java) {
                itemService.createItem(item)
            }

            assertEquals("Postal code must be specified", exception.message)
            verifyNoInteractions(itemRepository)
        }

        @Test
        @DisplayName("should fail to create item with empty title")
        fun `should fail to create item with empty title`() {
            val item = createValidItem().copy(title = "")

            val exception = assertThrows(IllegalArgumentException::class.java) {
                itemService.createItem(item)
            }

            assertEquals("Title must be specified", exception.message)
            verifyNoInteractions(itemRepository)
        }

        @Test
        @DisplayName("should fail to create item with empty description")
        fun `should fail to create item with empty description`() {
            val item = createValidItem().copy(description = "")

            val exception = assertThrows(IllegalArgumentException::class.java) {
                itemService.createItem(item)
            }

            assertEquals("Description must be specified", exception.message)
            verifyNoInteractions(itemRepository)
        }

        @Test
        @DisplayName("should fail to create item with negative price")
        fun `should fail to create item with negative price`() {
            val item = createValidItem().copy(price = -100.0)

            val exception = assertThrows(IllegalArgumentException::class.java) {
                itemService.createItem(item)
            }

            assertEquals("Price must be zero or positive", exception.message)
            verifyNoInteractions(itemRepository)
        }

        @Test
        @DisplayName("should fail to create item with invalid status")
        fun `should fail to create item with invalid status`() {
            val item = createValidItem().copy(status = "invalid-status")

            val exception = assertThrows(IllegalArgumentException::class.java) {
                itemService.createItem(item)
            }

            assertEquals("Invalid status", exception.message)
            verifyNoInteractions(itemRepository)
        }
    }

}