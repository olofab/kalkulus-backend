package com.timla.repository

import com.timla.model.Offer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OfferRepository : JpaRepository<Offer, Long> {

    fun findByCompanyId(companyId: Long): List<Offer>
    fun findByIdAndCompanyId(id: Long, companyId: Long): Offer?

}