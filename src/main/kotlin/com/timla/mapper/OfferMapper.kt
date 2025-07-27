package com.timla.mapper

import com.timla.mapper.ItemMapper
import com.timla.dto.ItemDTO
import com.timla.dto.OfferResponse
import com.timla.model.Offer
import com.timla.repository.CompanyRepository

object OfferMapper {
    fun toResponse(offer: Offer, vat: Double): OfferResponse {
        val totalSum = offer.items.sumOf { it.unitPrice * it.quantity }
        val totalSumWithVat = if (offer.includeVat) totalSum * (1 + vat / 100) else totalSum

        return OfferResponse(
            id = offer.id,
            title = offer.title,
            status = offer.status,
            customer = offer.customer,
            contactPerson = offer.contactPerson,
            phone = offer.phone,
            email = offer.email,
            address = offer.address,
            description = offer.description,
            validUntil = offer.validUntil,
            createdAt = offer.createdAt,
            updatedAt = offer.updatedAt,
            includeVat = offer.includeVat,
            totalSum = totalSum,
            totalSumWithVat = totalSumWithVat,
            notes = offer.notes ?: null,
            items = offer.items.map { ItemMapper.toDto(it) }
        )
    }
}

