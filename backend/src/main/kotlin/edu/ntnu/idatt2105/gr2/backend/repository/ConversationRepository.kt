package edu.ntnu.idatt2105.gr2.backend.repository


import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ConversationRepository(private val dataSource: DataSource) {

    fun save(conversation: Conversation): Conversation {
        dataSource.connection.use { conn ->
            val sql = "INSERT INTO conversations (item_id, buyer_id) VALUES (?, ?)"
            val stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)
            stmt.use {
                it.setInt(1, conversation.itemId)
                it.setInt(2, conversation.buyerId)

                if (it.executeUpdate() == 0) {
                    throw RuntimeException("Creating conversation failed, no rows affected.")
                }

                val generatedKeys = it.generatedKeys
                if (generatedKeys.next()) {
                    val generatedId = generatedKeys.getInt(1)

                    val selectSql = "SELECT * FROM conversations WHERE id = ?"
                    conn.prepareStatement(selectSql).use { selectStmt ->
                        selectStmt.setInt(1, generatedId)
                        selectStmt.executeQuery().use { rows ->
                            if (rows.next()) {
                                return Conversation(
                                    id = rows.getInt("id"),
                                    itemId = rows.getInt("item_id"),
                                    buyerId = rows.getInt("buyer_id"),
                                    createdAt = rows.getTimestamp("created_at").toInstant(),
                                    updatedAt = rows.getTimestamp("updated_at").toInstant()
                                )
                            }
                        }
                    }
                } else {
                    throw RuntimeException("Creating conversation failed, no ID obtained.")
                }
            }
        }
        throw RuntimeException("Creating conversation failed, no data retrieved.")
    }

    fun findConversationById(id: Int): Conversation? {
        dataSource.connection.use { conn ->
            val sql = "SELECT * FROM conversations WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Conversation(
                            id = rows.getInt("id"),
                            itemId = rows.getInt("item_id"),
                            buyerId = rows.getInt("buyer_id"),
                            createdAt = rows.getTimestamp("created_at").toInstant(),
                            updatedAt = rows.getTimestamp("updated_at").toInstant()
                        )
                    }
                }
            }
        }

        return null
    }

    fun findConversationByParticipants(userId1: Int, userId2: Int, itemId: Int): Conversation? {
        dataSource.connection.use { conn ->
            val sql = """
            SELECT c.* FROM conversations c
            JOIN items i ON c.item_id = i.id
            WHERE c.item_id = ?
              AND ((c.buyer_id = ? AND i.seller_id = ?) OR
                   (c.buyer_id = ? AND i.seller_id = ?))
            LIMIT 1
        """
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, itemId)
                stmt.setInt(2, userId1)
                stmt.setInt(3, userId2)
                stmt.setInt(4, userId2)
                stmt.setInt(5, userId1)
                stmt.executeQuery().use { rows ->
                    if (rows.next()) {
                        return Conversation(
                            id = rows.getInt("id"),
                            itemId = rows.getInt("item_id"),
                            buyerId = rows.getInt("buyer_id"),
                            createdAt = rows.getTimestamp("created_at").toInstant(),
                            updatedAt = rows.getTimestamp("updated_at").toInstant()
                        )
                    }
                }
            }
        }
        return null
    }


    fun findAllConversationsByUserId(userId: Int): List<Conversation> {
        dataSource.connection.use { conn ->
            val sql = """
            SELECT * FROM conversations
            WHERE buyer_id = ?
            ORDER BY updated_at DESC
        """
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, userId)
                stmt.executeQuery().use { rows ->
                    val conversations = mutableListOf<Conversation>()
                    while (rows.next()) {
                        conversations.add(
                            Conversation(
                                id = rows.getInt("id"),
                                itemId = rows.getInt("item_id"),
                                buyerId = rows.getInt("buyer_id"),
                                createdAt = rows.getTimestamp("created_at").toInstant(),
                                updatedAt = rows.getTimestamp("updated_at").toInstant()
                            )
                        )
                    }
                    return conversations
                }
            }
        }
    }

    fun delete(id: Int) {
        dataSource.connection.use { conn ->
            val sql = "DELETE FROM conversations WHERE id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                var affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Deleting conversation failed, no rows affected.")
                }
            }
        }
    }
}