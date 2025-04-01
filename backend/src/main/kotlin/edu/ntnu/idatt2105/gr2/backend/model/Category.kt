package edu.ntnu.idatt2105.gr2.backend.model

class Category(
    private var name: String,
    private var description: String
) {

    fun getName(): String {
        return name
    }

    fun getDescription(): String {
        return description
    }

    fun setDescription(description: String) {
        if (description.isBlank()) {
            throw IllegalArgumentException("Description cannot be blank")
        }
        this.description = description
    }
}