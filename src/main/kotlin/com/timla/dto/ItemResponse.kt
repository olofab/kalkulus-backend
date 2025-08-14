package com.timla.dto

data class ItemResponse(
    val id: Long,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
)