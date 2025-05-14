package com.kalkulus.controller

import com.kalkulus.model.Item
import com.kalkulus.model.Offer
import com.kalkulus.repository.ItemRepository
import com.kalkulus.repository.OfferRepository
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/offers")
class OfferController(
    private val offerRepo: OfferRepository,
    private val itemRepo: ItemRepository
) {

    @GetMapping
    fun getAll(): List<Offer> = offerRepo.findAll()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<Offer> =
        offerRepo.findById(id).map { ResponseEntity.ok(it) }.orElse(ResponseEntity.notFound().build())

    @PostMapping
    fun createOffer(@RequestBody offer: Offer): Offer {
        offer.items.forEach { it.offer = offer }
        return offerRepo.save(offer)
    }

    @PutMapping("/{id}")
    fun updateOffer(@PathVariable id: Long, @RequestBody updated: Offer): ResponseEntity<Offer> {
        val existing = offerRepo.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()
        existing.title = updated.title
        existing.status = updated.status
        existing.customer = updated.customer
        existing.updatedAt = java.time.LocalDateTime.now()
        return ResponseEntity.ok(offerRepo.save(existing))
    }

    @DeleteMapping("/{id}")
    fun deleteOffer(@PathVariable id: Long): ResponseEntity<String> {
        return if (offerRepo.existsById(id)) {
            offerRepo.deleteById(id)
            ResponseEntity.ok("Deleted")
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/item")
    @Transactional
    fun addItemToOffer(@PathVariable id: Long, @RequestBody item: Item): ResponseEntity<Offer> {
        val offer = offerRepo.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()
        item.offer = offer
        offer.items.add(item)
        return ResponseEntity.ok(offerRepo.save(offer))
    }
}
