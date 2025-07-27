
package com.timla.repository

import com.timla.model.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    fun existsByOrganizationNumber(organizationNumber: String): Boolean
}

