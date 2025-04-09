// File: ItemMapper.kt
package edu.ntnu.idatt2105.gr2.backend.dto

import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.model.ItemStatus
import java.time.Instant

internal fun CreateItemRequest.toItem(userId: Int): Item {
    return Item(
        sellerId = userId,
        categoryId = this.categoryId,
        postalCode = this.postalCode,
        title = this.title,
        description = this.description,
        price = this.price,
        purchasePrice = null,
        buyerId = null,
        location = this.location,
        allowVippsBuy = this.allowVippsBuy,
        primaryImageId = -1,
        status = ItemStatus.Available,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
}
