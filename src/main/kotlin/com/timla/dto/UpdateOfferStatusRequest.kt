package com.timla.dto

import com.timla.model.OfferStatus
import jakarta.validation.constraints.NotNull

data class UpdateOfferStatusRequest(
    @field:NotNull(message = "Status er påkrevd")
    val status: OfferStatus
)
