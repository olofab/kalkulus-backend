package com.timla.controller

import com.timla.model.ItemTemplate
import com.timla.repository.ItemTemplateRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/items/templates")
class ItemTemplateController(
    private val repository: ItemTemplateRepository
) {

    @GetMapping
    fun getAll(): List<ItemTemplate> = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody item: ItemTemplate): ItemTemplate =
        repository.save(item)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody updated: ItemTemplate
    ): ItemTemplate {
        val existing = repository.findById(id).orElseThrow {
            NoSuchElementException("Vare med ID $id ble ikke funnet")
        }

        existing.name = updated.name
        existing.unitPrice = updated.unitPrice

        return repository.save(existing)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        if (!repository.existsById(id)) {
            throw NoSuchElementException("Vare med ID $id finnes ikke")
        }
        repository.deleteById(id)
    }
}
