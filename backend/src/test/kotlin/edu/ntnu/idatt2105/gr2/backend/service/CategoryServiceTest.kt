package edu.ntnu.idatt2105.gr2.backend.service
import edu.ntnu.idatt2105.gr2.backend.config.TestConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
import java.sql.Connection


@Testcontainers
@SpringBootTest
@Sql(scripts = ["classpath:testdb/testData.sql"])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig::class)
@ActiveProfiles("test")
class CategoryServiceTest {

    companion object { private val db = MariaDBContainer<Nothing>("mariadb").apply {
            withDatabaseName("test")
            withUsername("mariadb")
            withPassword("mariadb")
        }

        @BeforeAll
        @JvmStatic
        fun startTestDBContainer() {
            db.start()
        }

        @AfterAll
        @JvmStatic
        fun stopTestDBContainer() {
            db.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }


    @Autowired
    private lateinit var categoryService: CategoryService

    @BeforeEach
    fun setup() {
        categoryService.deleteAllCategories()
        categoryService.createCategory("ELECTRONICS", "A description of electronics")
        categoryService.createCategory("CLOTHING", "A description of clothing")
        categoryService.createCategory("FURNITURE", "A description of furniture")
        categoryService.createCategory("BOOKS", "A description of books")
    }

    @Test
    @DisplayName("Is dbContainer running")
    fun `is dbContainer running`() {
        assert(db.isRunning)

    }

    @Nested
    @DisplayName("Positive tests")
    inner class PositiveTests{
        @Test
        @DisplayName("Test get all categories")
        fun `test get all categories`() {
            val categories = categoryService.getCategories()
            assertEquals(4, categories.size)
        }
        @Test
        @DisplayName("Test get category by name")
        fun `test get category by name`() {
            val category = categoryService.getCategory("ELECTRONICS")
            assertEquals("ELECTRONICS", category?.name)
        }
        @Test
        @DisplayName("Test create category")
        fun `test create category`() {
            categoryService.createCategory("New Category", "A description of Category")
            val categories = categoryService.getCategories()
            val newCategory = categoryService.getCategory("New Category")
            assertAll(
                { assertEquals(5, categories.size) },
                { assertEquals("New Category", categories[4].name) }
            )
        }

        @Test
        @DisplayName("Create category assigns correct id")
        fun `create category gives correct id`() {
            val category = categoryService.createCategory("New Category", "A description of Category")
            assertEquals(5, category.id)
        }

        @Test
        @DisplayName("Test update description")
        fun `test update category`() {
            categoryService.updateDescription("ELECTRONICS", "A brand new Description")
            val category = categoryService.getCategory("ELECTRONICS")
            assertEquals("A brand new Description", category?.description)
        }
        @Test
        @DisplayName("Test delete category")
        fun `test delete category`() {
            categoryService.deleteCategory("ELECTRONICS")
            val categories = categoryService.getCategories()
            assertEquals(3, categories.size)
        }
    }

    @Nested
    @DisplayName("Negative tests")
    inner class NegativeTests{
        @Test
        @DisplayName("Test get category by name")
        fun `test get category by name`() {
            val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                categoryService.getCategory("")
            }
            assertEquals("Name cannot be blank", exception.message)
        }
        @Test
        @DisplayName("Test create category")
        fun `test create category`() {
            val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                categoryService.createCategory("", "A description of Category")
            }
            assertEquals("Name cannot be blank", exception.message)
        }
        @Test
        @DisplayName("Test update description")
        fun `test update category`() {
            val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                categoryService.updateDescription("ELECTRONICS", "")
            }
            assertEquals("Name cannot be blank", exception.message)
        }
        @Test
        @DisplayName("Test delete category throws exception")
        fun `test delete category`() {
            val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                categoryService.deleteCategory("")
            }
            assertEquals("Name cannot be blank", exception.message)
        }
    }
}