package com.timla.controller

import com.timla.model.Item
import com.timla.model.Offer
import com.timla.repository.OfferRepository
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class OfferTestController(
    private val offerRepository: OfferRepository
) {
    @PostMapping("/delete-all")
    fun deleteAll(): String {
        offerRepository.deleteAll()
        return "Alle tilbud og produkter er slettet."
    }

    @DeleteMapping("/item/{itemId}")
    @Transactional
    fun deleteItem(@PathVariable itemId: Long): String {
        val offers = offerRepository.findAll()
        offers.forEach { it.items.removeIf { item -> item.id == itemId } }
        offerRepository.saveAll(offers)
        return "Item $itemId deleted"
    }

    @PostMapping("/item")
    @Transactional
    fun addItemToFirstOffer(@RequestBody newItem: Item): String {
        val offer = offerRepository.findAll().firstOrNull()
            ?: return "Ingen tilbud finnes"

        newItem.offer = offer
        offer.items.add(newItem)
        offerRepository.save(offer)

        return "Item lagt til i '${offer.title}'"
    }
}
