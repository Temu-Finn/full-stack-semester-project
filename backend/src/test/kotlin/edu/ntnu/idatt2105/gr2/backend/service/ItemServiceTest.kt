package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.config.TestConfig
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant

// Enable Testcontainers support for integration testing with a "real" db
@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig::class)
@Sql(scripts = ["classpath:testdb/testData.sql"])
@Sql(
    scripts = ["classpath:testdb/insertTestData.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class ItemServiceTest {
    companion object {
        private val db =
            MariaDBContainer<Nothing>("mariadb").apply {
                withDatabaseName("test")
                withUsername("mariadb")
                withPassword("mariadb")
            }

        @BeforeAll
        @JvmStatic
        fun startContainer() {
            db.start()
        }

        @AfterAll
        @JvmStatic
        fun stopContainer() {
            db.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDBProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }

    @Autowired
    private lateinit var itemService: ItemService

    @Autowired
    private lateinit var itemRepository: ItemRepository

    private lateinit var testItem1: Item
    private lateinit var testItem2: Item
    private lateinit var itemWithNegData: Item

    @BeforeEach
    fun setUp() {
        testItem1 =
            Item(
                // id = 0,
                sellerId = 1,
                categoryId = 1,
                postalCode = "7014",
                title = "Test Item",
                description = "Test description",
                price = 99.95,
                purchasePrice = null,
                buyerId = null,
                location = Pair(10.0, 20.0),
                allowVippsBuy = true,
                primaryImageId = null,
                status = "available",
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            )

        testItem2 =
            Item(
                // id = 1,
                sellerId = 2,
                categoryId = 2,
                postalCode = "7014",
                title = "Test Item2",
                description = "Test description2",
                price = 10.25,
                purchasePrice = null,
                buyerId = null,
                location = Pair(2.0, 3.0),
                allowVippsBuy = false,
                primaryImageId = null,
                status = "available",
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            )

        itemService.deleteAllItems()
    }

    @Nested
    @DisplayName("Positive tests")
    inner class PositiveTest {
        @Test
        fun `repository should load items from DB`() {
            itemService.createItem(testItem1)
            val allItems = itemRepository.findAllByCategoryId(categoryId = 1)
            println(allItems.size)
        }

        @Test
        fun `should create item successfully`() {
            val createdItem = itemService.createItem(testItem1)
            Assertions.assertNotNull(createdItem.id)
            Assertions.assertEquals("Test Item", createdItem.title)
        }

        @Test
        fun `should get item by  id`() {
            itemService.createItem(testItem1)
            itemService.createItem(testItem2)

            val itemFromId = itemService.getItemById(1)
            Assertions.assertNotNull(itemFromId)
            Assertions.assertEquals("Test Item", itemFromId!!.title)
        }

        @Test
        fun `should delete item by id`() {
            itemService.createItem(testItem1)
            itemService.createItem(testItem2)

            itemService.deleteItemById(1)
            val itemId1 = itemService.getItemById(1)
            Assertions.assertNull(itemId1, "Item with id 1 should be deleted")
        }

        @Test
        fun `should get item by category id`() {
            itemService.createItem(testItem1)
            itemService.createItem(testItem2)

            val itemCategoryId1 = itemService.getItemsByCategoryId(1)
            Assertions.assertEquals(1, itemCategoryId1.size)
        }

        @Test
        fun `should get all existing items`() {
            itemService.createItem(testItem1)
            itemService.createItem(testItem2)

            val allExistingItems = itemService.getAllItems()
            Assertions.assertEquals(2, allExistingItems.size)
        }

        @Test
        fun `should delete all items`() {
            itemService.createItem(testItem1)
            itemService.createItem(testItem2)

            itemService.deleteAllItems()
            val allExistingItemsAfterDeleting = itemService.getAllItems()

            Assertions.assertEquals(0, allExistingItemsAfterDeleting.size)
        }
    }

    @Nested
    @DisplayName("Negative tests")
    inner class NegativeTest {
        @Test
        fun `should not create item when price is negative`() {
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                val itemWithNegData =
                    Item(
                        sellerId = 1,
                        categoryId = 2,
                        postalCode = "7014",
                        title = "Test Item2",
                        description = "Test description2",
                        price = -4.9, // This triggers the exception
                        purchasePrice = null,
                        buyerId = null,
                        location = Pair(2.0, 3.0),
                        allowVippsBuy = false,
                        primaryImageId = null,
                        status = "available",
                        createdAt = Instant.now(),
                        updatedAt = Instant.now(),
                    )

                itemService.createItem(itemWithNegData)
            }

            val allExistingItems = itemService.getAllItems()
            Assertions.assertEquals(0, allExistingItems.size)
        }

        @Test
        fun `should return null when getting item by non-existing id`() {
            val item = itemService.getItemById(999L)
            Assertions.assertNull(item, "Item with id 999 should be returned null")
        }

        @Test
        fun `should not delete anything when deleting non-existing item`() {
            itemService.createItem(testItem1)
            itemService.createItem(testItem2)

            itemService.deleteItemById(999L)

            val allItems = itemService.getAllItems()
            Assertions.assertEquals(2, allItems.size)
        }

        @Test
        fun `should not throw when deleting non-existing item`() {
            Assertions.assertDoesNotThrow {
                itemService.deleteItemById(999L)
            }
        }
    }
}
