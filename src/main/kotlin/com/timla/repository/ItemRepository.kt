package com.timla.repository

import com.timla.model.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Long> {
  fun findByOfferId(offerId: Long): List<Item>
}
