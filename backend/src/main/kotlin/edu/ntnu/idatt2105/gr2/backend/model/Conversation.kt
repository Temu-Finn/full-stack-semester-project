package edu.ntnu.idatt2105.gr2.backend.model

import java.sql.Timestamp

data class Conversation(
    val id: Int = -1,
    val itemId: Int,
    val buyerId: Int,
    val createdAt: Timestamp,
    val updatedAt: Timestamp
)
{
    init {
        require(itemId >= 0) { "Item ID must be non-negative" }
        require(buyerId >= 0) { "Buyer ID must be non-negative" }
        require(createdAt.time > 0) { "Created time must be a valid timestamp" }
        require(updatedAt.time > 0) { "Updated time must be a valid timestamp" }
    }
}