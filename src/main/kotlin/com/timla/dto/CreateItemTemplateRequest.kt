package com.timla.dto

data class CreateItemTemplateRequest(
    val name: String,
    val unitPrice: Double,
    val categoryIds: List<Long> = emptyList()
)