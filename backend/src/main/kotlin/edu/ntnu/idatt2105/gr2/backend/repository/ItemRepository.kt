package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.SearchRequest
import edu.ntnu.idatt2105.gr2.backend.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

@Repository
class ItemRepository(private val dataSource: DataSource) {

    fun create(item: Item): Item {
        dataSource.connection.use { conn ->
            val sql = """
                INSERT INTO items (seller_id, category_id, postal_code, title, description, price, location, allow_vipps_buy, status) 
                VALUES(?, ?, ?, ?, ?, ?, ST_PointFromText(?, 4326), ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setInt(1, item.sellerId)
                stmt.setInt(2, item.categoryId)
                stmt.setString(3, item.postalCode)
                stmt.setString(4, item.title)
                stmt.setString(5, item.description)
                stmt.setDouble(6, item.price)
                stmt.setString(7, item.location?.let { "POINT(${it.longitude} ${it.latitude})" })
                stmt.setBoolean(8, item.allowVippsBuy)
                stmt.setString(9, item.status.toString())

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

    fun setPrimaryImage(itemId: Int, imageId: Int) {
        val sql = "UPDATE items SET primary_image_id = ? WHERE id = ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, imageId)
                stmt.setInt(2, itemId)
                stmt.executeUpdate()
            }
        }
    }

    fun getItemById(id: Int): Item? {
        return queryItemsWhere("id = ?") { it.setInt(1, id) }
            .firstOrNull()
    }

    fun findAllBySellerId(sellerId: Int, isOwnUser: Boolean): List<Item> {
        return if (isOwnUser) {
            queryItemsWhere("seller_id = ?") { it.setInt(1, sellerId) }
        } else {
            queryItemsWhere("seller_id = ? AND status = ?") { it.setInt(1, sellerId); it.setString(2, ItemStatus.Available.toString()) }
        }
    }

    fun deleteById(id: Int): Boolean =
        executeUpdateAndReturnCount("DELETE FROM items WHERE id = ?") { it.setInt(1, id) } > 0

    fun findRecommendedItems(): List<Item> {
        val sql = """
            $selectSql
            $tablesSql
            WHERE i.status = ?
            ORDER BY RAND()
            LIMIT 10
        """.trimIndent()

        val cards = mutableListOf<Item>()
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, ItemStatus.Available.toString())
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        cards.add(mapRowToItem(rs))
                    }
                }
            }
        }
        return cards
    }

    fun searchItems(request: SearchRequest, pageable: Pageable): Page<Item> {
        val whereClause = request.whereClause()
        val baseSql = """
            $tablesSql
            WHERE $whereClause
        """.trimIndent()

        // Query for total count
        val countSql = "SELECT COUNT(i.id) $baseSql"
        val totalCount = dataSource.connection.use { conn ->
            conn.prepareStatement(countSql).use { stmt ->
                request.prepareWhereClause(stmt)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getLong(1) else 0L
                }
            }
        }

        // Query for page content
        val sortClause = pageable.sort.map { "${it.property} ${it.direction}" }.joinToString(", ").ifBlank { "i.updated_at DESC" }
        val contentSql = """
            $selectSql
            $baseSql
            ORDER BY $sortClause
            LIMIT ? OFFSET ? 
        """.trimIndent()
        
        val content = dataSource.connection.use { conn ->
            conn.prepareStatement(contentSql).use { stmt ->
                var paramIndex = request.prepareWhereClause(stmt)
                stmt.setInt(paramIndex++, pageable.pageSize)
                stmt.setLong(paramIndex, pageable.offset)
                
                stmt.executeQuery().use { rs ->
                    buildList { while (rs.next()) { add(mapRowToItem(rs)) } }
                }
            }
        }

        return PageImpl(content, pageable, totalCount)
    }

    fun findFavoriteByUserId(userId: Int): List<Item> {
        val sql = """
        $selectSql
        FROM favorites f
        JOIN items i ON f.item_id = i.id
        JOIN postal_codes pc ON i.postal_code = pc.postal_code
        WHERE f.user_id = ?
    """.trimIndent()

        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, userId)
                stmt.executeQuery().use { rs ->
                    buildList { while (rs.next()) add(mapRowToItem(rs)) }
                }
            }
        }
    }

    private fun queryItemsWhere(
        where: String,
        setParams: (java.sql.PreparedStatement) -> Unit = {}
    ): List<Item> {
        val effectiveWhere = if (where.isBlank()) "1=1" else where
        val sql = """
            $selectSql
            $tablesSql
            WHERE $effectiveWhere
        """.trimIndent()
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                setParams(stmt)
                stmt.executeQuery().use { rs ->
                    buildList { while (rs.next()) { add(mapRowToItem(rs)) } }
                }
            }
        }
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
            status = rs.getItemStatus(),
            createdAt = rs.getTimestamp("created_at").toInstant(),
            updatedAt = rs.getTimestamp("updated_at").toInstant(),
            municipality = rs.getString("municipality"),
        )
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

    fun findAllBought(userId: Int): List<Item> {
        return queryItemsWhere("buyer_id = ? AND status = ?") { it.setInt(1, userId); it.setString(2, ItemStatus.Sold.toString()) }
    }

    fun findAllReserved(userId: Int): List<Item> {
        return queryItemsWhere("buyer_id = ? AND status = ?") { it.setInt(1, userId); it.setString(2, ItemStatus.Reserved.toString()) }
    }

    fun reserveItem(itemId: Int, userId: Int) {
        val sql = "UPDATE items SET status = ?, buyer_id = ? WHERE id = ? AND status = ? RETURNING *"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, ItemStatus.Reserved.toString())
                stmt.setInt(2, userId)
                stmt.setInt(3, itemId)
                stmt.setString(4, ItemStatus.Available.toString())
                stmt.executeUpdate()
            }
        }
    }

    private val tablesSql: String = """
        FROM items i
        JOIN postal_codes pc ON i.postal_code = pc.postal_code
    """.trimIndent()

    private val selectSql = "SELECT i.*, ST_X(location) AS longitude, ST_Y(location) AS latitude, pc.*"
}