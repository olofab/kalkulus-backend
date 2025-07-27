package com.timla.mapper

import com.timla.dto.CategoryDto
import com.timla.dto.ItemTemplateResponse
import com.timla.model.ItemTemplate

object ItemTemplateMapper {
    fun toResponse(item: ItemTemplate): ItemTemplateResponse {
        return ItemTemplateResponse(
            id = item.id,
            name = item.name,
            unitPrice = item.unitPrice,
            categories = item.categories.map {
                CategoryDto(id = it.id, name = it.name)
            }
        )
    }
}
