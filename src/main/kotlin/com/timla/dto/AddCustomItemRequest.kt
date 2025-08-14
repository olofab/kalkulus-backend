package com.timla.dto

data class AddCustomItemRequest(
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val categoryId: Long? = null
)
