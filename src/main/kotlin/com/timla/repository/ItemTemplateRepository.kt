package com.timla.repository

import com.timla.model.ItemTemplate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ItemTemplateRepository : JpaRepository<ItemTemplate, Long> {
    fun findByCompanyId(companyId: Long): List<ItemTemplate>
    fun findByIdAndCompanyId(id: Long, companyId: Long): ItemTemplate?

}