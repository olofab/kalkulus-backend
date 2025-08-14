package com.timla.service

import com.timla.dto.RegisterCompanyRequest
import com.timla.dto.UserResponse
import com.timla.model.Company
import com.timla.model.User
import com.timla.model.UserType
import com.timla.repository.CompanyRepository
import com.timla.repository.UserRepository
import com.timla.security.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepo: UserRepository,
    private val companyRepo: CompanyRepository,
    private val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun registerCompany(req: RegisterCompanyRequest): Map<String, Any> {
        if (companyRepo.existsByOrganizationNumber(req.organizationNumber)) {
            throw IllegalArgumentException("Organisasjonsnummeret er allerede i bruk.")
        }

        val company = companyRepo.save(
            Company(
                name = req.companyName,
                organizationNumber = req.organizationNumber,
                industry = req.industry
            )
        )

        val user = userRepo.save(
            User(
                name = req.name,
                email = req.email,
                passwordHash = passwordEncoder.encode(req.password),
                userType = UserType.ADMIN,
                isAdmin = true,
                company = company
            )
        )

        val token = jwtUtil.generateToken(user)

        return mapOf(
            "token" to token,
            "userId" to user.id,
            "companyId" to company.id
        )
    }

    fun login(email: String, password: String, companyId: Long): Map<String, String> {
        val user = userRepo.findByEmailAndCompanyId(email, companyId)
            ?: throw RuntimeException("Bruker ikke funnet i valgt selskap")

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw RuntimeException("Feil passord")
        }

        val token = jwtUtil.generateToken(user)
        return mapOf("token" to token)
    }
}
