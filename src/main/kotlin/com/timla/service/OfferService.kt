package com.timla.service

import com.timla.dto.OfferResponse
import com.timla.dto.UpdateOfferRequest
import com.timla.mapper.OfferMapper
import com.timla.model.Offer
import com.timla.repository.CompanyRepository
import com.timla.repository.OfferRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OfferService(
    private val offerRepository: OfferRepository,
    private val companyRepository: CompanyRepository
) {
    fun getAllOffers(companyId: Long): List<OfferResponse> {
        val vat = companyRepository.findById(companyId).get().vat ?: 0.0
        return offerRepository.findByCompanyId(companyId).map { OfferMapper.toResponse(it, vat) }
    }

    fun getOfferById(id: Long, companyId: Long): OfferResponse? {
        val vat = companyRepository.findById(companyId).get().vat ?: 0.0
        return offerRepository.findByIdAndCompanyId(id, companyId)?.let { OfferMapper.toResponse(it, vat) }
    }

    fun createOffer(offer: Offer, companyId: Long): OfferResponse {
        offer.companyId = companyId
        val saved = offerRepository.save(offer)
        val vat = companyRepository.findById(companyId).get().vat ?: 0.0
        return OfferMapper.toResponse(saved, vat)
    }

    fun updateOffer(id: Long, req: UpdateOfferRequest, companyId: Long): ResponseEntity<OfferResponse> {
        val offer = offerRepository.findByIdAndCompanyId(id, companyId)
            ?: return ResponseEntity.notFound().build()

        offer.title = req.title
        offer.status = req.status
        offer.customer = req.customer
        offer.contactPerson = req.contactPerson
        offer.phone = req.phone
        offer.email = req.email
        offer.address = req.address
        offer.description = req.description
        offer.validUntil = req.validUntil
        offer.includeVat = req.includeVat
        offer.updatedAt = LocalDateTime.now()

        val saved = offerRepository.save(offer)
        val vat = companyRepository.findById(companyId).get().vat ?: 0.0
        return ResponseEntity.ok(OfferMapper.toResponse(saved, vat))
    }

    fun deleteOffer(id: Long, companyId: Long): ResponseEntity<Void> {
        val offer = offerRepository.findByIdAndCompanyId(id, companyId)
            ?: return ResponseEntity.notFound().build()
        offerRepository.delete(offer)
        return ResponseEntity.noContent().build()
    }

    fun searchOffers(query: String, companyId: Long): List<OfferResponse> {
        val lowerQuery = query.lowercase()
        val offers = offerRepository.findByCompanyId(companyId)
        val vat = companyRepository.findById(companyId).get().vat ?: 0.0

        return offers.filter { offer ->
            val matchesBasicFields = listOfNotNull(
                offer.title,
                offer.description,
                offer.customer,
                offer.contactPerson,
                offer.address,
                offer.email,
                offer.phone,
                offer.notes
            ).any { it.lowercase().contains(lowerQuery) }

            val matchesItems = offer.items.any { it.name.lowercase().contains(lowerQuery) }

            matchesBasicFields || matchesItems
        }.map { OfferMapper.toResponse(it, vat) }
    }
}