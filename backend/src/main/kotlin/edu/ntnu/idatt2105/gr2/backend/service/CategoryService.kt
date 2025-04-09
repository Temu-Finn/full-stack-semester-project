package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.CategoryResponse
import edu.ntnu.idatt2105.gr2.backend.dto.CreateCategoryRequest
import edu.ntnu.idatt2105.gr2.backend.dto.UpdateCategoryRequest
import edu.ntnu.idatt2105.gr2.backend.model.Category
import edu.ntnu.idatt2105.gr2.backend.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService (
    private val categoryRepository: CategoryRepository
)  {

    fun createCategory(request: CreateCategoryRequest): CategoryResponse {
        return categoryRepository.save(request.toModel()).toResponse()
    }

    fun getCategories(): List<CategoryResponse> {
        return categoryRepository.findAll().map { it.toResponse() }
    }

    fun getCategory(id: Int): CategoryResponse {
        return categoryRepository.getById(id)?.toResponse()
            ?: throw IllegalArgumentException("Category with id $id not found")
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

    fun updateCategory(updateCategoryRequest: UpdateCategoryRequest): CategoryResponse {
        return categoryRepository.updateCategory(updateCategoryRequest.toModel()).toResponse()
    }
}

fun Category.toResponse(): CategoryResponse {
    return CategoryResponse(
        id = this.id,
        name = this.name,
        icon = this.icon,
        description = this.description
    )
}

fun CreateCategoryRequest.toModel(): Category {
    return Category(
        id = -1,
        name = this.name,
        icon = this.icon,
        description = this.description
    )
}

fun UpdateCategoryRequest.toModel(): Category {
    return Category(
        id = this.id,
        name = this.name,
        icon = this.icon,
        description = this.description
    )
}