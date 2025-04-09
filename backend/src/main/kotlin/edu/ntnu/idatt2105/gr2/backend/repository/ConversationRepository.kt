package edu.ntnu.idatt2105.gr2.backend.repository

import edu.ntnu.idatt2105.gr2.backend.dto.ConversationCardResponse
import edu.ntnu.idatt2105.gr2.backend.dto.ConversationsCardsResponse
import edu.ntnu.idatt2105.gr2.backend.dto.CreateConversationRequest
import edu.ntnu.idatt2105.gr2.backend.dto.CreateConversationResponse
import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ConversationRepository(private val dataSource: DataSource) {

    fun save(conversation: Conversation): CreateConversationResponse {
        val itemId = conversation.itemId
        val buyerId = conversation.buyerId
        dataSource.connection.use { conn ->
            val sql = "INSERT INTO conversations (item_id, buyer_id) VALUES (?, ?)"
            conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setInt(1, itemId)
                stmt.setInt(2, buyerId)

                val affectedRows = stmt.executeUpdate()
                if (affectedRows == 0) {
                    throw RuntimeException("Creating conversation failed, no rows affected.")
                }

                stmt.generatedKeys.use { keys ->
                    if (keys.next()) {
                        val generatedId = keys.getInt(1)
                        // Retrieve the full conversation, including timestamps
                        val selectSql = "SELECT * FROM conversations WHERE id = ?"
                        conn.prepareStatement(selectSql).use { selectStmt ->
                            selectStmt.setInt(1, generatedId)
                            selectStmt.executeQuery().use { rows ->
                                if (rows.next()) {
                                    return CreateConversationResponse("Conversation created successfully with ID: $generatedId"
                                            +" at ${rows.getTimestamp("created_at")}")
                                }
                            }
                        }
                    }
                }
            }
        }
        throw RuntimeException("Creating conversation failed, no ID obtained.")
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
                            createdAt = rows.getTimestamp("created_at").toLocalDateTime(),
                            updatedAt = rows.getTimestamp("updated_at").toLocalDateTime()
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
                                createdAt = rows.getTimestamp("created_at").toLocalDateTime(),
                                updatedAt = rows.getTimestamp("updated_at").toLocalDateTime()
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