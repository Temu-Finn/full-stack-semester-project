package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.model.ItemStatus
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
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ST_PointFromText(?, 4326), ?, ?, ?)
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
                stmt.setString(9, item.location?.let { "POINT(${it.first} ${it.second})" })
                stmt.setBoolean(10, item.allowVippsBuy)
                stmt.setObject(11, item.primaryImageId)
                stmt.setString(12, item.status.toString())

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
        queryItemsWhere("id = ?") { it.setInt(1, id) }.firstOrNull()

    fun findAllByCategoryId(categoryId: Int): List<Item> =
        queryItemsWhere("category_id = ?") { it.setInt(1, categoryId) }

    fun findAllBySellerId(sellerId: Int): List<Item> =
        queryItemsWhere("seller_id = ?") { it.setInt(1, sellerId) }

    fun deleteById(id: Int): Boolean =
        executeUpdateAndReturnCount("DELETE FROM items WHERE id = ?") { it.setInt(1, id) } > 0

    fun findRecommendedItems(): List<ItemCard> {
        val sql = """
            SELECT i.id, i.title, i.price, pc.municipality, ii.image_data, ii.file_type
            FROM items i
            JOIN postal_codes pc ON i.postal_code = pc.postal_code
            LEFT JOIN item_images ii ON ii.id = i.primary_image_id
            WHERE i.status = ?
            ORDER BY RAND()
            LIMIT 10
        """.trimIndent()

        val cards = mutableListOf<ItemCard>()
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, ItemStatus.Available.toString())
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
        return Item(
            id = rs.getInt("id"),
            sellerId = rs.getInt("seller_id"),
            categoryId = rs.getInt("category_id"),
            postalCode = rs.getString("postal_code"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            price = rs.getDouble("price"),
            purchasePrice = rs.getDoubleOrNull("purchase_price"),
            buyerId = rs.getIntOrNull("buyer_id"),
            location = rs.getLocation(),
            allowVippsBuy = rs.getBoolean("allow_vipps_buy"),
            primaryImageId = rs.getIntOrNull("primary_image_id"),
            status = ItemStatus.fromString(rs.getString("status")),
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

    private fun queryItemsWhere(
        where: String,
        setParams: (java.sql.PreparedStatement) -> Unit = {}
    ): List<Item> {
        val sql = """
            SELECT id, seller_id, category_id, postal_code, title, description, price, purchase_price, buyer_id, 
                   ST_X(location) AS longitude, ST_Y(location) AS latitude, allow_vipps_buy, primary_image_id, 
                   status, created_at, updated_at
            FROM items
            WHERE $where
        """.trimIndent()
        
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                setParams(stmt)
                stmt.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(mapRowToItem(rs))
                        }
                    }
                }
            }
        }
    }

    private fun executeUpdateAndReturnCount(
        sql: String,
        setParams: (java.sql.PreparedStatement) -> Unit = {}
    ): Int {
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                setParams(stmt)
                stmt.executeUpdate()
            }
        }
    }
}