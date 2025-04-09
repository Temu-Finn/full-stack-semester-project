package edu.ntnu.idatt2105.gr2.backend.model

import java.time.Instant

data class Conversation(
    val id: Int = -1,
    val itemId: Int,
    val buyerId: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)
{
    init {
        require(itemId >= 0) { "Item ID must be non-negative" }
        require(buyerId >= 0) { "Buyer ID must be non-negative" }
    }
}