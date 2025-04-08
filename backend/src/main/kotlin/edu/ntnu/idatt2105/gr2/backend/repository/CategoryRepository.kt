package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Category
import org.springframework.stereotype.Repository
import javax.sql.DataSource

// SQL repository for categories
@Repository
class CategoryRepository (private val dataSource: DataSource) {

    // Saves a category
    fun save(category: Category) : Category {
        val name = category.name
        val icon = category.icon
        val description = category.description

        if (existsByName(name)) {
            throw IllegalArgumentException("Category with name $name already exists")
        }

        dataSource.connection.use { conn ->
            val sql = "INSERT INTO categories (name, icon, description) VALUES (?, ?, ?)"
            conn.prepareStatement(sql,  java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, icon)
                stmt.setString(3, description)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating category failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        return category.copy(id = keys.getInt(1))
                    } else {
                        throw RuntimeException("Creating category failed," +
                                " no ID could be obtained.")
                    }
                }
            }
        }
    }

    fun deleteAll() {
        dataSource.connection.use { conn ->
            val sql = "DELETE FROM categories"
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeUpdate()
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
                        categories.add(Category(rows.getInt("id"),
                            rows.getString("name"),
                            rows.getString("icon"),
                            rows.getString("description")))
                    }
                    return categories
                }
            }
        }
    }

    // Finds a category by name
    fun findByName(name: String): Category? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM categories WHERE name = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Category(
                            rows.getInt("id"),
                            rows.getString("name"),
                            rows.getString("icon"),
                            rows.getString("description")
                        )
                    }
                }
            }
        }
        return null
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
    fun updateCategory(category: Category): Category {
        if (!existsByName(category.name)) {
            throw IllegalArgumentException("Category with name $category.name does not exist")
        }
        dataSource.connection.use { conn ->
            val sql = "UPDATE categories SET name = ?, description = ?, icon = ? WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, category.name)
                stmt.setString(2, category.description)
                stmt.setString(3, category.icon)
                stmt.setInt(4, category.id)
                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Updating category failed, no rows affected.")
                }

                return category
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