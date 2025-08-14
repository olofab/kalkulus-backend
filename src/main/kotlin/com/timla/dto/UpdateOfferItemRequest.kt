package com.timla.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateOfferItemRequest(
    @field:NotBlank(message = "Navn kan ikke være tomt")
    @field:Size(max = 255, message = "Navn kan ikke være lengre enn 255 tegn")
    val name: String,
    
    @field:Min(1, message = "Antall må være minst 1")
    val quantity: Int,
    
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Pris må være større enn 0")
    val unitPrice: Double,
    
    val categoryId: Long?
)
