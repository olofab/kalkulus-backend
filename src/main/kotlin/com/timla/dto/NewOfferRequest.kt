package com.timla.dto

import jakarta.validation.constraints.*
import java.time.LocalDate

data class NewOfferRequest(
    @field:NotBlank(message = "Kundenavn er påkrevd")
    val customer: String,

    @field:NotBlank(message = "Kontaktperson er påkrevd")
    val contactPerson: String,

    @field:NotBlank(message = "Telefonnummer er påkrevd")
    @field:Pattern(regexp = "^[0-9\\-+ ]{6,20}$", message = "Ugyldig telefonnummer")
    val phone: String,

    @field:NotBlank(message = "E-post er påkrevd")
    @field:Email(message = "Ugyldig e-postadresse")
    val email: String,

    @field:NotBlank(message = "Adresse er påkrevd")
    val address: String,

    @field:NotBlank(message = "Tittel er påkrevd")
    val title: String,

    val description: String? = null,

    val validUntil: LocalDate? = null,

    @field:NotBlank
    val status: String = "draft"
)
