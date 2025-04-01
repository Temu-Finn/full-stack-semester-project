package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api/items")
class ItemController(private val itemService: ItemService) {

    @PostMapping
    fun createItem(@RequestBody item: Item): ResponseEntity<Item> {
        val savedItem = itemService.createItem(item)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem)
    }

    @GetMapping("/category/{id}")
    fun getItemsByCategoryId(@PathVariable id: Long): ResponseEntity<List<Item>> {
        val items = itemService.getItemsByCategoryId(id)
        return ResponseEntity.ok(items)
    }

    @DeleteMapping("/{id}")
    fun deleteItemById(@PathVariable id: Long): ResponseEntity<Void> {
        return if (itemService.deleteItemById(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}