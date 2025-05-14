package com.kalkulus.repository

import com.kalkulus.model.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<Item, Long>
