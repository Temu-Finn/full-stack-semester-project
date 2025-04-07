package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.service.CategoryService
import edu.ntnu.idatt2105.gr2.backend.dto.CategoriesResponse
import edu.ntnu.idatt2105.gr2.backend.dto.CategoryResponse
import edu.ntnu.idatt2105.gr2.backend.dto.CreateCategoryRequest
import edu.ntnu.idatt2105.gr2.backend.dto.DeleteCategoryRequest
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import edu.ntnu.idatt2105.gr2.backend.service.UserContextService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Category management APIs")
class CategoryController(
    private val categoryService: CategoryService,
    private val userContextService: UserContextService,
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)


    @GetMapping("/getAll")
    @Operation(
        summary = "Get all categories",
        description = "Returns a list of all categories"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getAllCategories(): ResponseEntity<CategoriesResponse> {
        logger.info("Fetching all categories...")

        val categories = categoryService.getCategories()

        logger.info("Successfully fetched all categories")
        return ResponseEntity.status(HttpStatus.OK).body(
            CategoriesResponse(
                categories.map { category ->
                    CategoryResponse(
                        category.id,
                        category.name,
                        category.description
                    )
                }
            )
        )
    }

    @GetMapping("/getCategoryByName/{name}")
    @Operation(
        summary = "Get category by name",
        description = "Returns a category by its name"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Category not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getCategoryByName(
        @Parameter(description = "Name of the category to retrieve", required = true)
        @PathVariable name: String
    ): ResponseEntity<CategoryResponse> {
        logger.info("Fetching category with name: $name")
        val category = categoryService.getCategory(name)

        if(category == null) {
            logger.error("Category with name: $name not found")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }

        logger.info("Successfully fetched category with name: $name")
        return ResponseEntity.status(HttpStatus.OK).body(
            CategoryResponse(
                category.id,
                category.name,
                category.description
            )
        )
    }

    @PostMapping("/create")
    @Operation(
        summary = "Create a new category",
        description = "Creates a new category with the given name and description"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Category created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "403", description = "User not authorized to create category"),
            ApiResponse(responseCode = "409", description = "Category already exists")
        ]
    )
    fun createCategory(
        @Parameter(description = "Name and description of the category to create", required = true)
        @RequestBody @Valid req: CreateCategoryRequest
    ): ResponseEntity<CategoryResponse> {
        val isAuthorized = userRepository.isAdmin(userContextService.getCurrentUserId())
        if (!isAuthorized) {
            logger.error("User not authorized to create category")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }

        logger.info("Creating new category with name: ${req.name}")
        val category = categoryService.createCategory(req.name, req.description)
        logger.info("Successfully created category with name: ${req.name}")

        return ResponseEntity.status(HttpStatus.CREATED).body(
            CategoryResponse(
                category.id,
                category.name,
                category.description
            )
        )
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "Delete a category",
        description = "Deletes a category by its name"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            ApiResponse(responseCode = "404", description = "Category not found"),
            ApiResponse(responseCode = "403", description = "User not authorized to delete category"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun deleteCategory(
        @Parameter(description = "Name of the category to delete", required = true)
        @RequestBody @Valid request: DeleteCategoryRequest
    ): ResponseEntity<Nothing> {
        val isAuthorized = userRepository.isAdmin(userContextService.getCurrentUserId())
        if (!isAuthorized) {
            logger.error("User not authorized to delete category")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }

        logger.info("Deleting category with name: ${request.name}")
        categoryService.deleteCategory(request.name)
        logger.info("Successfully deleted category with name: ${request.name}")

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }
}