package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.Category
import edu.ntnu.idatt2105.gr2.backend.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService (
    private val categoryRepository: CategoryRepository
)  {

    fun createCategory(name: String, description: String): Category {
        val category = Category(name = name, description =  description)
        return categoryRepository.save(category)
    }

    fun getCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    fun getCategory(name: String): Category? {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name cannot be blank")
        }
        return categoryRepository.findByName(name)
    }

    fun deleteCategory(name: String) {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name cannot be blank")
        }
        categoryRepository.delete(name)
    }

    fun deleteAllCategories() {
        categoryRepository.deleteAll()
    }

    fun updateDescription(name: String, description: String) {
        if (name.isBlank() || description.isBlank()) {
            throw IllegalArgumentException("Name cannot be blank")
        }
        categoryRepository.updateDescription(name, description)
    }
}