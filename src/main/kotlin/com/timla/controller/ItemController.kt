package com.timla.controller

import com.timla.dto.ItemUpdateRequest
import com.timla.model.Item
import com.timla.model.Offer
import com.timla.repository.ItemRepository
import com.timla.repository.OfferRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ItemController(
    private val offerRepository: OfferRepository,
    private val itemRepository: ItemRepository
) {

    // 1. Legg til vare i et tilbud
    @PostMapping("/offers/{offerId}/item")
    @ResponseStatus(HttpStatus.CREATED)
    fun addItem(
        @PathVariable offerId: Long,
        @RequestBody item: Item
    ): Offer {
        val offer = offerRepository.findById(offerId).orElseThrow()
        item.offer = offer
        itemRepository.save(item)
        return offerRepository.findById(offerId).get()
    }

    // 2. Oppdater antall på en vare
    @PutMapping("/items/{itemId}")
    fun updateItemQuantity(
        @PathVariable itemId: Long,
        @RequestBody @Valid request: ItemUpdateRequest
    ): Item {
        val item = itemRepository.findById(itemId).orElseThrow()
        item.quantity = request.quantity
        return itemRepository.save(item)
    }

    // 3. Slett vare fra tilbud
    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteItem(@PathVariable itemId: Long) {
        val item = itemRepository.findById(itemId)
        .orElseThrow { RuntimeException("Item ikke funnet") }
     itemRepository.delete(item)
    }
}
