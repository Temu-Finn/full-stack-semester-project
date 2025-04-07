package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.model.getImageDataUrl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

@Repository
class ItemRepository(private val dataSource: DataSource) {

    fun create(item: Item): Item {
        dataSource.connection.use { conn ->
            val sql = """
                INSERT INTO items (seller_id, category_id, postal_code, title, description, price, purchase_price, buyer_id, location, allow_vipps_buy, primary_image_id, status) 
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ST_SRID(?, 4326), ?, ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setInt(1, item.sellerId)
                stmt.setInt(2, item.categoryId)
                stmt.setString(3, item.postalCode)
                stmt.setString(4, item.title)
                stmt.setString(5, item.description)
                stmt.setDouble(6, item.price)
                stmt.setObject(7, item.purchasePrice)
                stmt.setObject(8, item.buyerId)
                stmt.setString(9, item.location?.let { "POINT(${it.first},${it.second})" } )
                stmt.setBoolean(10, item.allowVippsBuy)
                stmt.setObject(11, item.primaryImageId)
                stmt.setString(12, item.status)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) throw RuntimeException("Creating item failed, no rows affected.")

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val id = keys.getInt(1)
                        return item.copy(id = id)
                    } else {
                        throw RuntimeException("Creating item failed, no ID obtained.")
                    }
                }
            }
        }
    }

    fun getItemById(id: Int): Item? =
        queryItems("SELECT * FROM items WHERE id = ?") { it.setInt(1, id) }.firstOrNull()

    fun findAllByCategoryId(categoryId: Int): List<Item> =
        queryItems("SELECT * FROM items WHERE category_id = ?") { it.setInt(1, categoryId) }

    fun deleteById(id: Int): Boolean =
        executeUpdateAndReturnCount("DELETE FROM items WHERE id = ?") { it.setInt(1, id) } > 0

    fun findAllByOwner(ownerId: Int): List<Item> {
        return queryItems("SELECT * FROM items WHERE seller_id = ?") {
            it.setInt(1, ownerId)
        }
    }

    fun deleteAll() {
        executeUpdateAndReturnCount("DELETE FROM items")
    }

    fun findRecommendedItems(userId: Int): List<ItemCard> {
        val sql = """
            SELECT i.id, i.title, i.price, pc.municipality, ii.image_data, ii.file_type
            FROM items i
            JOIN postal_codes pc ON i.postal_code = pc.postal_code
            LEFT JOIN item_images ii ON ii.id = i.primary_image_id
            ORDER BY RAND()
            LIMIT 10
        """.trimIndent()

        val cards = mutableListOf<ItemCard>()
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        cards.add(mapRowToItemCard(rs))
                    }
                }
            }
        }
        return cards
    }

    private fun mapRowToItem(rs: ResultSet): Item {
        val location: Pair<Double, Double>? = null // TODO: parse location from geometry if needed
        return Item(
            id = rs.getInt("id"),
            sellerId = rs.getInt("seller_id"),
            categoryId = rs.getInt("category_id"),
            postalCode = rs.getString("postal_code"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            price = rs.getDouble("price"),
            purchasePrice = rs.getDouble("purchase_price"),
            buyerId = rs.getInt("buyer_id"),
            location = location,
            allowVippsBuy = rs.getBoolean("allow_vipps_buy"),
            primaryImageId = rs.getInt("primary_image_id"),
            status = rs.getString("status"),
            createdAt = rs.getTimestamp("created_at")?.toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at")?.toLocalDateTime()
        )
    }

    private fun mapRowToItemCard(rs: ResultSet): ItemCard {
        return ItemCard(
            itemId = rs.getInt("id"),
            title = rs.getString("title"),
            price = rs.getDouble("price"),
            municipality = rs.getString("municipality"),
            imageBase64 = rs.getImageDataUrl()
        )
    }

    private fun queryItems(
        sql: String,
        setParams: (java.sql.PreparedStatement) -> Unit = {}
    ): List<Item> {
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

    private fun executeUpdateAndReturnCount(
        sql: String,
        setParams: (java.sql.PreparedStatement) -> Unit = {}
    ): Int {
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                setParams(stmt)
                return stmt.executeUpdate()
            }
        }
    }
}