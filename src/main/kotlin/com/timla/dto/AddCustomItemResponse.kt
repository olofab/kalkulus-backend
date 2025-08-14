package com.timla.dto

data class AddCustomItemResponse(
    val itemId: Long,
    val name: String,
    val unitPrice: Double,
    val quantity: Int
)
