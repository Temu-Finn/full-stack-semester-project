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

    fun findAllByCategoryId(categoryId: Long): List<Item> {
        val items = mutableListOf<Item>()
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM items WHERE category_id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, categoryId)
                stmt.executeQuery().use { rows ->
                    while (rows.next()) {
                        val location = rows.getObject("location")?.let {
                            val point = it.toString()
                                .removePrefix("POINT(")
                                .removeSuffix(")")
                                .split(" ")
                                .map { coord -> coord.toDouble() }
                            Pair(point[0], point[1])
                        }

                        items.add(
                            Item(
                                id = rows.getLong("id"),
                                sellerId = rows.getLong("seller_id"),
                                categoryId = rows.getLong("category_id"),
                                postalCode = rows.getString("postal_code"),
                                title = rows.getString("title"),
                                description = rows.getString("description"),
                                price = rows.getDouble("price"),
                                purchasePrice = rows.getObject("purchase_price") as? Double,
                                buyerId = rows.getObject("buyer_id") as? Long,
                                location = location,
                                allowVippsBuy = rows.getBoolean("allow_vipps_buy"),
                                primaryImageId = rows.getObject("primary_image_id") as? Long,
                                status = rows.getString("status"),
                                createdAt = rows.getTimestamp("created_at")?.toLocalDateTime(),
                                updatedAt = rows.getTimestamp("updated_at")?.toLocalDateTime()
                            )
                        )
                    }
                }
            }
        }
        return items
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

    fun deleteAll() {
        dataSource.connection.use { conn ->
            val sql = "DELETE FROM items"
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeUpdate()
            }
        }
    }
}