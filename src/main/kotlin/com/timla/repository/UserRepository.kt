
package com.timla.repository

import com.timla.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmailAndCompanyId(email: String, companyId: Long): User?

    fun findAllByEmail(email: String): List<User>

    fun findByEmail(email: String): User?

    fun findByCompanyId(companyId: Long): List<User>

}