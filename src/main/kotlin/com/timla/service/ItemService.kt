package com.timla.service

import com.timla.model.Item
import com.timla.repository.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository
) {

    fun findItemById(itemId: Long): Item? = itemRepository.findById(itemId).orElse(null)

    fun deleteItem(item: Item) = itemRepository.delete(item)
}