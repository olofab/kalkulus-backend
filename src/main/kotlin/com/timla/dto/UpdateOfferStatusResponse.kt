package com.timla.dto

import com.timla.model.OfferStatus
import java.time.LocalDateTime

data class UpdateOfferStatusResponse(
    val success: Boolean,
    val offer: OfferStatusDto
)

data class OfferStatusDto(
    val id: Long,
    val status: OfferStatus,
    val updatedAt: LocalDateTime
)
