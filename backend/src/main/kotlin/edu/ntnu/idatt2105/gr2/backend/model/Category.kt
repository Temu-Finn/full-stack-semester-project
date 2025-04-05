package edu.ntnu.idatt2105.gr2.backend.model

data class Category(
    val id: Int = -1,
    val name: String,
    var description: String
) {
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(description.isNotBlank()) { "Description cannot be blank" }
    }
}