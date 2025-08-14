package com.timla.dto

import com.timla.model.OfferStatus
import java.time.LocalDate

data class UpdateOfferRequest(
    val title: String,
    val status: OfferStatus,
    val customer: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val address: String,
    val description: String?,
    val validUntil: LocalDate?,
    val includeVat: Boolean
)