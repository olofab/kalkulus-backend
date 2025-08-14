package com.timla.dto

data class UpdateOfferItemResponse(
    val success: Boolean,
    val itemId: Long,
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val categoryId: Long?
)
