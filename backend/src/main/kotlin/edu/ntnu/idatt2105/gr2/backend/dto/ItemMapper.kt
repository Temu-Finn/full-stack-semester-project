// File: ItemMapper.kt
package edu.ntnu.idatt2105.gr2.backend.dto

import edu.ntnu.idatt2105.gr2.backend.model.Item
import java.time.LocalDateTime

fun CreateItemRequest.toItem(): Item {
    return Item(
        sellerId = this.sellerId,
        categoryId = this.categoryId,
        postalCode = this.postalCode,
        title = this.title,
        description = this.description,
        price = this.price,
        purchasePrice = this.purchasePrice,
        buyerId = this.buyerId,
        location = this.location,
        allowVippsBuy = this.allowVippsBuy,
        primaryImageId = this.primaryImageId,
        status = this.status,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}

fun Item.toResponse(): ItemResponse {
    return ItemResponse(
        id = this.id,
        sellerId = this.sellerId,
        categoryId = this.categoryId,
        postalCode = this.postalCode,
        title = this.title,
        description = this.description,
        price = this.price,
        purchasePrice = this.purchasePrice,
        buyerId = this.buyerId,
        location = this.location,
        allowVippsBuy = this.allowVippsBuy,
        primaryImageId = this.primaryImageId,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
