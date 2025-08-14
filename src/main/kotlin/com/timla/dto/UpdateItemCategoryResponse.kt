package com.timla.dto

data class UpdateItemCategoryResponse(
    val success: Boolean,
    val itemId: Long,
    val categoryId: Long
)
