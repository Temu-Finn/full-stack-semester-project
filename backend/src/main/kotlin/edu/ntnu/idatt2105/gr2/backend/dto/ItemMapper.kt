// File: ItemMapper.kt
package edu.ntnu.idatt2105.gr2.backend.dto

import edu.ntnu.idatt2105.gr2.backend.model.Item
import java.time.LocalDateTime

internal fun CreateItemRequest.toItem(userId: Int): Item {
    return Item(
        sellerId = userId,
        categoryId = this.categoryId,
        postalCode = this.postalCode,
        title = this.title,
        description = this.description,
        price = this.price,
        purchasePrice = this.purchasePrice,
        buyerId = this.buyerId,
        location = this.location?.let {Pair(it.latitude, it.longitude)},
        allowVippsBuy = this.allowVippsBuy,
        primaryImageId = this.primaryImageId,
        status = this.status,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}

internal fun Item.toResponse(): ItemResponse {
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
        location = this.location?.let { Location(it.first, it.second) },
        allowVippsBuy = this.allowVippsBuy,
        primaryImageId = this.primaryImageId,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
