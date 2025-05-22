package com.timla.repository

import com.timla.model.ItemTemplate
import org.springframework.data.jpa.repository.JpaRepository

interface ItemTemplateRepository : JpaRepository<ItemTemplate, Long>
