package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Category
import org.springframework.stereotype.Repository
import javax.sql.DataSource

// SQL repository for categories
@Repository
class CategoryRepository (private val dataSource: DataSource) {

    // Saves a category
    fun save(category: Category) : Category {
        val name = category.getName()
        val description = category.getDescription()

        if (existsByName(name)) {
            throw IllegalArgumentException("Category with name $name already exists")
        }

        dataSource.connection.use { conn ->
            val sql = "INSERT INTO categories (name, description) VALUES (?, ?)"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, description)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating category failed, no rows affected.")
                }

                return Category(name, description)
            }
        }
    }

    // Finds all categories
    fun findAll(): List<Category> {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM categories"
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rows ->
                    val categories = mutableListOf<Category>()
                    while (rows.next()) {
                        categories.add(Category(rows.getString("name"), rows.getString("description")))
                    }
                    return categories
                }
            }
        }
    }

    // Finds a category by name
    fun findByName(name: String): Category {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM categories WHERE name = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Category(rows.getString("name"), rows.getString("description"))
                    }
                }
            }
        }
        throw RuntimeException("Category not found")
    }

    // Deletes a category
    fun delete(name: String) {
        if (!existsByName(name)) {
            throw IllegalArgumentException("Category with name $name does not exist")
        }
        dataSource.connection.use { conn ->
            val sql = "DELETE FROM categories WHERE name = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Deleting category failed, no rows affected.")
                }
            }
        }
    }

    // Updates the description of a category
    fun updateDescription(description: String, name: String) {
        if (!existsByName(name)) {
            throw IllegalArgumentException("Category with name $name does not exist")
        }
        dataSource.connection.use { conn ->
            val sql = "UPDATE categories SET description = ? WHERE name = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, description)
                stmt.setString(2, name)
                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Updating category failed, no rows affected.")
                }
            }
        }
    }

    // Checks if a category exists by name
    fun existsByName(name: String): Boolean {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM categories WHERE name = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rows ->
                    return rows.next()
                }
            }
        }
    }
}