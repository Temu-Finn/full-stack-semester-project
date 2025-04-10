package edu.ntnu.idatt2105.gr2.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CategoryResponse(
    val id: Int,
    val name: String,
    val icon: String,
    val description: String
)

data class CategoriesResponse(
    val categories: List<CategoryResponse>
)

data class CreateCategoryRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String,

    @field:NotBlank(message = "Icon cannot be empty")
    val icon: String,

    val description: String
)

data class UpdateCategoryRequest(
    @field:Positive(message = "Id must be positive")
    val id: Int,

    @field:NotBlank(message = "Name cannot be empty")
    val name: String,

    @field:NotBlank(message = "Icon cannot be empty")
    val icon: String,

    @field:NotBlank(message = "Description cannot be empty")
    val description: String
)

data class DeleteCategoryRequest(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String
)