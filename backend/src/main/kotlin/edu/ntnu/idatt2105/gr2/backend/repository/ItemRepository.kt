package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.SearchItemRequest
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
            SELECT id, seller_id, category_id, i.postal_code, title, description, price, purchase_price, buyer_id, 
                   ST_X(location) AS longitude, ST_Y(location) AS latitude, allow_vipps_buy, primary_image_id, 
                   status, created_at, updated_at, municipality
            FROM items i
            JOIN postal_codes pc ON i.postal_code = pc.postal_code
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

    fun searchItems(request: SearchItemRequest, pageable: Pageable): Page<Item> {
        val conditions = mutableListOf<String>("i.status = ?")
        val params = mutableListOf<Any>(ItemStatus.Available.toString())

        // Search text filtering 🔍
        if (!request.searchText.isNullOrBlank()) {
            conditions.add("(LOWER(i.title) LIKE LOWER(?) OR LOWER(i.description) LIKE LOWER(?))")
            params.add("%${request.searchText}%")
            params.add("%${request.searchText}%")
        }

        // Category filtering 📦
        if (request.categoryId != null) {
            conditions.add("i.category_id = ?")
            params.add(request.categoryId)
        }

        // Postal code based filtering (county, municipality, city) 🏠
        if (!request.county.isNullOrBlank()) {
            conditions.add("pc.county = ?")
            params.add(request.county)
            if (!request.municipality.isNullOrBlank()) {
                conditions.add("pc.municipality = ?")
                params.add(request.municipality)
                if (!request.city.isNullOrBlank()) {
                    conditions.add("pc.city = ?")
                    params.add(request.city)
                }
            }
        }

        // Distance filtering 📍
        if (request.latitude != null && request.longitude != null && request.maxDistance != null && request.maxDistance > 0) {
            conditions.add("i.location IS NOT NULL AND ST_Distance_Sphere(location, ST_PointFromText(?, 4326)) <= ?")
            params.add("POINT(${request.longitude} ${request.latitude})")
            params.add(request.maxDistance * 1000) // Convert km to meters
        }

        val whereClause = conditions.joinToString(" AND ")
        val baseSql = """
            FROM items i
            JOIN postal_codes pc ON i.postal_code = pc.postal_code
            WHERE $whereClause
        """.trimIndent()

        // Query for total count
        val countSql = "SELECT COUNT(i.id) $baseSql"
        val totalCount = dataSource.connection.use { conn ->
            conn.prepareStatement(countSql).use { stmt ->
                params.forEachIndexed { index, param -> stmt.setObject(index + 1, param) }
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getLong(1) else 0L
                }
            }
        }

        // Query for page content
        val sortClause = pageable.sort.map { "${it.property} ${it.direction}" }.joinToString(", ").ifBlank { "i.updated_at DESC" }
        val contentSql = """
            SELECT id, seller_id, category_id, i.postal_code, title, description, price, purchase_price, buyer_id, 
                   ST_X(location) AS longitude, ST_Y(location) AS latitude, allow_vipps_buy, primary_image_id, 
                   status, created_at, updated_at, municipality
            $baseSql
            ORDER BY $sortClause
            LIMIT ? OFFSET ? 
        """.trimIndent()
        
        val content = dataSource.connection.use { conn ->
            conn.prepareStatement(contentSql).use { stmt ->
                var paramIndex = 1
                params.forEach { param -> stmt.setObject(paramIndex++, param) }
                stmt.setInt(paramIndex++, pageable.pageSize)
                stmt.setLong(paramIndex++, pageable.offset)
                
                stmt.executeQuery().use { rs ->
                    buildList { while (rs.next()) { add(mapRowToItem(rs)) } }
                }
            }
        }

        return PageImpl(content, pageable, totalCount)
    }

    private fun queryItemsWhere(
        where: String,
        setParams: (java.sql.PreparedStatement) -> Unit = {}
    ): List<Item> {
        val effectiveWhere = if (where.isBlank()) "1=1" else where
        val sql = """
            SELECT id, seller_id, category_id, i.postal_code, title, description, price, purchase_price, buyer_id, 
                   ST_X(location) AS longitude, ST_Y(location) AS latitude, allow_vipps_buy, primary_image_id, 
                   status, created_at, updated_at, municipality
            FROM items i
            JOIN postal_codes pc ON i.postal_code = pc.postal_code
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
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime(),
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
}