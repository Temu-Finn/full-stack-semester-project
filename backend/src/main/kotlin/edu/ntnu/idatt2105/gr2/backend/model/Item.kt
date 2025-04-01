package edu.ntnu.idatt2105.gr2.backend.model

    class Item (
        val id: Long = -1,
        val owner: Long,
        val category: Long,
        val name: String,
        var price: Double,
        var description: String,
        var isSold: Boolean,
        var condition: String
    )