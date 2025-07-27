package com.timla.repository

import com.timla.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCompanyId(companyId: Long): List<Category>
    fun findByIdAndCompanyId(id: Long, companyId: Long): Category?
    fun findAllById(id: Long): List<Category>
}


