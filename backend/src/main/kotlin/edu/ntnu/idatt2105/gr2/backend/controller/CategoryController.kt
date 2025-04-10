package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.service.CategoryService
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

/**
 * Controller for setting up endpoints receiving category-related requests from client.
 * Controller includes getting all categories, creating, updating, deleting and getting
 * a category by id.
 *
 * @constructor                 Creates a new instance of CategoryController.
 * @param categoryService       The service for handling category-related operations.
 * @param userContextService    The service for handling user context operations.
 * @param userRepository        The repository for handling user-related operations.
 */

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Category management APIs")
class CategoryController(
    private val categoryService: CategoryService,
    private val userContextService: UserContextService,
    private val userRepository: UserRepository,
) {

    private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    /**
     * Handles client requests to get all categories. It delegates the request to the
     * service layer and returns the list of categories.
     *
     * @return   ResponseEntity CategoriesResponse       contains the list of categories
     */

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all categories",
        description = "Returns a list of all categories",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun getAllCategories(): ResponseEntity<CategoriesResponse> {
        logger.info("Fetching all categories...")

        val categories = categoryService.getCategories()

        logger.info("Successfully fetched all categories")
        return ResponseEntity.status(HttpStatus.OK).body(CategoriesResponse(categories))
    }

    /**
     * Handles client requests to get a category by id. It delegates the request to the
     * service layer and returns status and category as requested from service layer.
     *
     * @param id   Int                                   The id of the category to retrieve.
     * @return     ResponseEntity<CategoryResponse>      Contains the category details and status
     */

    @GetMapping("/{id}")
    @Operation(
        summary = "Get category by id",
        description = "Returns a category by its id",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Category not found"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun getCategoryById(
        @Parameter(description = "Id of the category to retrieve", required = true)
        @PathVariable id: Int
    ): ResponseEntity<CategoryResponse> {
        logger.info("Fetching category with id: $id")
        val category = categoryService.getCategory(id)
        logger.info("Successfully fetched category with id: $id")
        return ResponseEntity.status(HttpStatus.OK).body(category)
    }

    /**
     * Handles client requests to create a new category. It delegates the request to the
     * service layer and returns status and the created category from service layer.
     *
     * @param req   CreateCategoryRequest                 The request object containing category details.
     * @return      ResponseEntity<CategoryResponse>      Contains the created category details adn status
     */

    @PostMapping("/create")
    @Operation(
        summary = "Create a new category",
        description = "Creates a new category with the given name and description",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Category created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "403", description = "User not authorized to create category"),
            ApiResponse(responseCode = "409", description = "Category already exists"),
        ],
    )
    fun createCategory(
        @Parameter(description = "Name and description of the category to create", required = true)
        @RequestBody
        @Valid req: CreateCategoryRequest,
    ): ResponseEntity<CategoryResponse> {
        val isAuthorized = userRepository.isAdmin(userContextService.getCurrentUserId())
        if (!isAuthorized) {
            logger.error("User not authorized to create category")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }

        logger.info("Creating new category with name: ${req.name}")
        val category = categoryService.createCategory(req)
        logger.info("Successfully created category with name: ${req.name}")

        return ResponseEntity.status(HttpStatus.CREATED).body(category)
    }

    /**
     * Handles client requests to delete a category. It delegates the request to the
     * service layer and returns the status message to client.
     *
     * @param id   Int                                    The id of the category to delete.
     * @return     ResponseEntity<CategoryResponse>       Contains the deleted category details
     */

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a category",
        description = "Deletes a category by its name",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            ApiResponse(responseCode = "404", description = "Category not found"),
            ApiResponse(responseCode = "403", description = "User not authorized to delete category"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun deleteCategory(
        @Parameter(description = "Name of the category to delete", required = true)
        @PathVariable id: Int,
    ): ResponseEntity<Nothing> {
        val isAuthorized = userRepository.isAdmin(userContextService.getCurrentUserId())
        if (!isAuthorized) {
            logger.error("User not authorized to delete category")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }

        logger.info("Deleting category with name: $id")
        categoryService.deleteCategory(id)
        logger.info("Successfully deleted category with name: $id")

        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    /**
     * Handles client requests to delete all categories. It delegates the request to the
     * service layer and returns the updated category and status message to client.
     *
     * @param request    UpdateCategoryRequest               The id of the category to delete.
     * @return           ResponseEntity<CategoryResponse>    contains the deleted category details
     */

    @PutMapping("/update")
    @Operation(
        summary = "Update a category",
        description = "Updates a category by its name",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Category updated successfully"),
            ApiResponse(responseCode = "404", description = "Category not found"),
            ApiResponse(responseCode = "403", description = "User not authorized to update category"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun updateCategory(
        @Parameter(description = "Name of the category to update", required = true)
        @RequestBody
        @Valid request: UpdateCategoryRequest,
    ): ResponseEntity<CategoryResponse> {
        val isAuthorized = userRepository.isAdmin(userContextService.getCurrentUserId())
        if (!isAuthorized) {
            logger.error("User not authorized to update category")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }

        logger.info("Updating category with name: ${request.name}")
        val category = categoryService.updateCategory(request)
        logger.info("Successfully updated category with name: ${request.name}")

        return ResponseEntity.status(HttpStatus.OK).body(category)
    }
}