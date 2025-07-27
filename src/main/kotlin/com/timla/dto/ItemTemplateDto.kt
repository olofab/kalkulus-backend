package com.timla.dto

data class ItemTemplateDto(
    val name: String,
    val unitPrice: Double,
    val categories: List<CategoryDto>
)
