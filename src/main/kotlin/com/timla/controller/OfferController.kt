package com.timla.controller

import com.timla.dto.NewOfferRequest
import com.timla.dto.UpdateOfferRequest
import com.timla.model.Offer
import com.timla.repository.OfferRepository
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/offers")
@Validated
class OfferController(
    private val offerRepository: OfferRepository
) {


    @GetMapping
    fun getAllOffers(): List<Offer> = offerRepository.findAll()

    @GetMapping("/{id}")
    fun getOfferById(@PathVariable id: Long): Offer {
        return offerRepository.findById(id).orElseThrow {
            NoSuchElementException("Tilbud med ID $id ble ikke funnet")
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOffer(@RequestBody @Valid request: NewOfferRequest): Offer {
        val offer = Offer(
            customer = request.customer,
            contactPerson = request.contactPerson,
            phone = request.phone,
            email = request.email,
            address = request.address,
            title = request.title,
            description = request.description,
            validUntil = request.validUntil,
            status = request.status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return offerRepository.save(offer)
    }

    @PutMapping("/{id}")
    fun updateOffer(@PathVariable id: Long, @RequestBody @Valid updated: UpdateOfferRequest): Offer {
        val offer = offerRepository.findById(id).orElseThrow {
            NoSuchElementException("Tilbud med ID $id ble ikke funnet")
        }

        updated.title?.let { offer.title = it }
        updated.status?.let { offer.status = it }
        updated.description?.let { offer.description = it }
        updated.validUntil?.let { offer.validUntil = it }

        updated.customer?.let { offer.customer = it }
        updated.contactPerson?.let { offer.contactPerson = it }
        updated.phone?.let { offer.phone = it }
        updated.email?.let { offer.email = it }
        updated.address?.let { offer.address = it }

        offer.updatedAt = LocalDateTime.now()
        return offerRepository.save(offer)
    }
}
