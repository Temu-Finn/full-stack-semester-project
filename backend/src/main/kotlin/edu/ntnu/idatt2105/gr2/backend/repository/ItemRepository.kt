package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.ItemCard
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class ItemRepository (
    private val dataSource: DataSource,
) {

    fun findRecommendedItems(): List<ItemCard> {
        val sql = """
            SELECT
                i.id,
                i.title,
                i.price,
                pc.municipality,
                (SELECT ii.image_blob FROM item_images ii WHERE ii.item_id = i.id) AS primary_image_blob
            FROM
                items i
            JOIN
                postal_codes pc ON i.postal_code = pc.postal_code
            ORDER BY
                RAND() -- Or some other recommendation logic
            LIMIT 10; -- Limit the number of items
        """

        val items = mutableListOf<ItemCard>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        items.add(mapRowToItemCard(rs))
                    }
                }
            }
        }

        return items
    }

    private fun mapRowToItemCard(rs: ResultSet): ItemCard {
        val imageBlobBytes = rs.getBytes("primary_image_blob")
        val imageBase64 = imageBlobBytes?.let { java.util.Base64.getEncoder().encodeToString(it) }

        return ItemCard(
            itemId = rs.getInt("id"),
            title = rs.getString("title"),
            price = rs.getDouble("price"),
            municipality = rs.getString("municipality"),
            imageBase64 = imageBase64
        )
    }
}