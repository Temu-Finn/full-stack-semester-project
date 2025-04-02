package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.config.TestConfig
import edu.ntnu.idatt2105.gr2.backend.repository.CategoryRepository
import edu.ntnu.idatt2105.gr2.backend.model.Category
import edu.ntnu.idatt2105.gr2.backend.service.CategoryService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import javax.transaction.Transactional


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig::class)
class CategoryServiceTest {

    @Autowired
    private lateinit var categoryService: CategoryService

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setup() {
        //categoryRepository.deleteAll()
        categoryRepository.save(Category("ELECTRONICS", "A description of electronics"))
        categoryRepository.save(Category("CLOTHING", "A description of clothing"))
        categoryRepository.save(Category("FURNITURE", "A description of furniture"))
        categoryRepository.save(Category("BOOKS", "A description of books"))
    }

    @AfterEach
    fun cleanUp() {
        categoryRepository.delete("ELECTRONICS")
        categoryRepository.delete("CLOTHING")
        categoryRepository.delete("FURNITURE")
        categoryRepository.delete("BOOKS")
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
            assertEquals("ELECTRONICS", category.name)
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
        @DisplayName("Test update description")
        fun `test update category`() {
            categoryService.updateDescription("ELECTRONICS", "A brand new Description")
            val category = categoryService.getCategory("ELECTRONICS")
            assertEquals("A brand new Description", category.description)
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
        @DisplayName("Test delete category")
        fun `test delete category`() {
            val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                categoryService.deleteCategory("")
            }
            assertEquals("Name cannot be blank", exception.message)
        }
    }
}