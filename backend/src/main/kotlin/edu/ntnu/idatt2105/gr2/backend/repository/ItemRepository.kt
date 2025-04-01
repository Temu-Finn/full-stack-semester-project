package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.User
import org.springframework.stereotype.Repository
import java.sql.Statement
import javax.sql.DataSource

@Repository
class ItemRepository(private val dataSource: DataSource) {
    fun create(item: Item): Item{
        val ownerId = item.owner
        val categoryId = item.category
        val itemName = item.name
        val price = item.price
        val description = item.description
        // val picture = item.picture
        val isSold = item.isSold
        val itemCondition = item.condition
        dataSource.connection.use { conn ->
            val sql = "INSERT INTO items (owner_id, category_id, item_name, price, description, is_sold, item_condition) VALUES(?,?,?,?,?,?)"
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setLong(1, ownerId)
                stmt.setLong(2, categoryId)
                stmt.setString(3, itemName)
                stmt.setDouble(4, price)
                stmt.setString(5, description)
                stmt.setBoolean(6, isSold)
                stmt.setString(7, itemCondition)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating item failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val id = keys.getLong(1)
                        return item.copy(id = id)
                    } else {
                        throw RuntimeException("Creating item failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun findAllByCategoryId(categoryId: Long): Item? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM items WHERE category_id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, categoryId.toString())
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Item(
                            rows.getLong("id"),
                            rows.getLong("owner"),
                            rows.getLong("category"),
                            rows.getDouble("price"),
                            rows.getString("description"),
                            rows.getBoolean("is_sold"),
                            rows.getString("condition")
                        )
                    }
                }
            }
        }

        return null
    }

    fun deleteById(id: Long): Boolean {
        dataSource.connection.use { conn ->
            val sql = "DELETE FROM items WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, id)
                val affectedRows = stmt.executeUpdate()
                return affectedRows > 0
            }
        }
    }
}