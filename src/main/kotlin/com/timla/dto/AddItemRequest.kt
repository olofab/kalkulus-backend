package com.timla.dto

data class AddItemRequest(
    val templateId: Long,
    val quantity: Int
)