package com.timla.dto

data class ItemDTO(
    val id: Long,
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val offerId: Long? = null // optional; only used if needed
)
