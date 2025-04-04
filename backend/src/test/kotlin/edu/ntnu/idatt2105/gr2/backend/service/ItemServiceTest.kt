package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.config.TestConfig
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
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
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import org.junit.jupiter.api.*
import org.springframework.test.context.jdbc.Sql

// Enable Testcontainers support for integration testing with a "real" db
@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)


@ActiveProfiles("test")
@Import(TestConfig::class)
@Sql(scripts = ["classpath:testdb/testData.sql"])
@Sql(
    scripts = ["classpath:testdb/insertTestData.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)



class ItemServiceTest {
    companion object {
        private val db = MariaDBContainer<Nothing>("mariadb").apply {
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

    private lateinit var testItem: Item

    @BeforeEach
    fun setUp() {
        testItem = Item(
            id = 4,
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
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
            )

        itemService.deleteAllItems()
        //itemService.createItem(testItem)
    }

    @Nested
    @DisplayName("Positive tests")
    inner class PositiveTest {

        @Test
        fun `should create item successfully`() {
            val createdItem = itemService.createItem(testItem)
            Assertions.assertNotNull(createdItem.id)
            Assertions.assertEquals("Test Item", createdItem.title)
        }

        @Test
        fun `should get item by category id`() {
        }
    }
}
