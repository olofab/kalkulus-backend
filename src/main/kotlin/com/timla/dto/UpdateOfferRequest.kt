package com.timla.dto

import jakarta.validation.constraints.Email
import java.time.LocalDate

data class UpdateOfferRequest(
    val title: String? = null,
    val status: String? = null,
    val description: String? = null,
    val validUntil: LocalDate? = null,

    val customer: String? = null,
    val contactPerson: String? = null,

    @field:Email(message = "Ugyldig e-postadresse")
    val email: String? = null,

    val phone: String? = null,
    val address: String? = null
)
