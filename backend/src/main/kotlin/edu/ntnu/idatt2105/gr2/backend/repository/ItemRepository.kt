package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Item
import org.springframework.stereotype.Repository
import java.sql.Statement
import javax.sql.DataSource

@Repository
class ItemRepository(private val dataSource: DataSource) {
    fun create(item: Item): Item{
        dataSource.connection.use { conn ->
            val sql = """
                INSERT INTO items (seller_id, category_id, postal_code, title, description, price, purchase_price, buyer_id, location, allow_vipps_buy, primary_image_id, status) 
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ST_PointFromText(?), ?, ?, ?)""".trimIndent()
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setLong(1, item.sellerId)
                stmt.setLong(2, item.categoryId)
                stmt.setString(3, item.postalCode)
                stmt.setString(4, item.title)
                stmt.setString(5, item.description)
                stmt.setDouble(6, item.price)
                stmt.setObject(7, item.purchasePrice)
                stmt.setObject(8, item.buyerId)
                stmt.setString(9, item.location?.let { "POINT(${it.first} ${it.second})" } ?: null)
                stmt.setBoolean(10, item.allowVippsBuy)
                stmt.setObject(11, item.primaryImageId)
                stmt.setString(12, item.status)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating item failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val id = keys.getLong(1)
                        return item.copy(id = id) //
                    } else {
                        throw RuntimeException("Creating item failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun getItemById(id: Long): Item? =
        queryItems("SELECT * FROM items WHERE id = ?") {
            it.setLong(1, id)
        }.firstOrNull()

    fun findAllByCategoryId(categoryId: Long): List<Item> {
        return queryItems("SELECT * FROM items WHERE category_id = ?") { stmt ->
            stmt.setLong(1, categoryId)
        }
    }

    fun deleteById(id: Long): Boolean {
        return executeUpdateAndReturnCount("DELETE FROM items WHERE id = ?") {
            it.setLong(1, id)
        } > 0
    }

    fun getAll(): List<Item> {
        return queryItems("SELECT * FROM items")
    }

    fun deleteAll() {
        executeUpdateAndReturnCount("DELETE FROM items")
    }

    private fun mapRowToItem(rs: java.sql.ResultSet): Item{
        // Temporary fix for location, doesn't handle Pair
        val location: Pair<Double, Double>? = null

        return Item(
            id = rs.getLong("id"),
            sellerId = rs.getLong("seller_id"),
            categoryId = rs.getLong("category_id"),
            postalCode = rs.getString("postal_code"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            price = rs.getDouble("price"),
            purchasePrice = rs.getObject("purchase_price") as? Double,
            buyerId = rs.getObject("buyer_id") as? Long,
            location = location,
            allowVippsBuy = rs.getBoolean("allow_vipps_buy"),
            primaryImageId = rs.getObject("primary_image_id") as? Long,
            status = rs.getString("status"),
            createdAt = rs.getTimestamp("created_at")?.toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at")?.toLocalDateTime()
        )
    }

    private fun queryItems(sql: String, setParams: (java.sql.PreparedStatement) -> Unit = {}): List<Item> {
        val items = mutableListOf<Item>()
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                setParams(stmt)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        items.add(mapRowToItem(rs))
                    }
                }
            }
        }
        return items
    }

    private fun executeUpdateAndReturnCount(sql: String, setParams: (java.sql.PreparedStatement) -> Unit = {}
    ): Int {
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                setParams(stmt)
                return stmt.executeUpdate()
            }
        }
    }
}