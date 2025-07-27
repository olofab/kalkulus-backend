package com.timla.dto

import java.time.LocalDate
import java.time.LocalDateTime
import com.timla.dto.ItemDTO


data class OfferResponse(
    val id: Long,
    val title: String,
    val status: String,
    val customer: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val address: String,
    val description: String?,
    val validUntil: LocalDate?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val includeVat: Boolean,
    val totalSum: Double,
    val totalSumWithVat: Double,
    val notes: String?,
    val items: List<ItemDTO>
)

