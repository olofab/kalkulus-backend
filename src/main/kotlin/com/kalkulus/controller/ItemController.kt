package com.kalkulus.controller

import com.kalkulus.repository.ItemRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/items")
class ItemController(
    private val itemRepo: ItemRepository
) {
    @DeleteMapping("/{itemId}")
    fun deleteItem(@PathVariable itemId: Long): ResponseEntity<String> {
        return if (itemRepo.existsById(itemId)) {
            itemRepo.deleteById(itemId)
            ResponseEntity.ok("Item deleted")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
