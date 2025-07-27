package com.timla.dto

data class ItemTemplateResponse(
    val id: Long,
    val name: String,
    val unitPrice: Double,
    val categories: List<CategoryDto>
)

data class CategoryDto(
    val id: Long,
    val name: String
)
