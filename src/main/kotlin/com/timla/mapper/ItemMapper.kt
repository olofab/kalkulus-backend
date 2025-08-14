package com.timla.mapper

import com.timla.dto.ItemDTO
import com.timla.model.Item

object ItemMapper {
    fun toDto(item: Item): ItemDTO {
        return ItemDTO(
            id = item.id,
            name = item.name,
            quantity = item.quantity,
            unitPrice = item.unitPrice,
            offerId = item.offer?.id
        )
    }

    fun fromDto(dto: ItemDTO): Item {
        return Item(
            id = dto.id,
            name = dto.name,
            quantity = dto.quantity,
            unitPrice = dto.unitPrice
            // offer is set elsewhere, not from DTO
        )
    }
}