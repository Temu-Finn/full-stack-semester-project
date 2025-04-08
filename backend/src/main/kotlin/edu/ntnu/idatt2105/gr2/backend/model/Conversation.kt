package edu.ntnu.idatt2105.gr2.backend.model

import java.sql.Timestamp

data class Conversation(
    val id: Int = -1,
    val itemId: Int,
    val buyerId: Int,
    val createdAt: Timestamp,
    val updatedAt: Timestamp
)