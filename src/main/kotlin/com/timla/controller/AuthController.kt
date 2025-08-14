package com.timla.controller

import com.timla.dto.LoginRequest
import com.timla.dto.RegisterCompanyRequest
import com.timla.model.Company
import com.timla.model.User
import com.timla.model.UserType
import com.timla.repository.CompanyRepository
import com.timla.repository.UserRepository
import com.timla.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepo: UserRepository,
    private val companyRepo: CompanyRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {

    // POST /api/auth/register-company
    @PostMapping("/register-company")
    fun registerCompany(@RequestBody req: RegisterCompanyRequest): ResponseEntity<Map<String, Any>> {
        // Sjekk om selskap allerede finnes
        if (companyRepo.existsByOrganizationNumber(req.organizationNumber)) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Organisasjonsnummer er allerede registrert"))
        }

        // Opprett selskap
        val company = Company(
            name = req.companyName,
            organizationNumber = req.organizationNumber,
            industry = req.industry
        )
        companyRepo.save(company)

        // Opprett admin-bruker
        val user = User(
            name = req.name,
            email = req.email,
            userType = UserType.ADMIN,
            passwordHash = passwordEncoder.encode(req.password),
            company = company,
            isAdmin = true
        )
        userRepo.save(user)

        // Generer token
        val token = jwtUtil.generateToken(user)

        return ResponseEntity.ok(
            mapOf(
                "token" to token,
                "userId" to user.id,
                "companyId" to company.id
            )
        )
    }

    @PostMapping("/email-check")
    fun checkEmail(@RequestBody payload: Map<String, String>): ResponseEntity<Any> {
    val email = payload["email"] ?: return ResponseEntity.badRequest().body("E-post mangler")

    val users = userRepo.findAllByEmail(email)

    return when {
        users.isEmpty() -> ResponseEntity.status(404).body("Ingen konto funnet")
        users.size == 1 -> ResponseEntity.ok(mapOf("companyId" to users[0].company.id, "companyName" to users[0].company.name))
        else -> ResponseEntity.ok(users.map {
            mapOf("companyId" to it.company.id, "companyName" to it.company.name)
        })
    }
    }   

    // POST /api/auth/login
    @PostMapping("/login")
fun login(@RequestBody req: LoginRequest): ResponseEntity<Map<String, Any>> {
    val users = userRepo.findAllByEmail(req.email)

    if (users.isEmpty()) {
        return ResponseEntity
            .badRequest()
            .body(mapOf("error" to "Fant ingen bruker med den e-posten"))
    }

    val matchingUser = users.find { passwordEncoder.matches(req.password, it.passwordHash) }

    if (matchingUser == null) {
        return ResponseEntity
            .badRequest()
            .body(mapOf("error" to "Feil passord eller selskap"))
    }

    val token = jwtUtil.generateToken(matchingUser)

    return ResponseEntity.ok(
        mapOf(
        "token" to token,
        "userId" to matchingUser.id,
        "companyId" to matchingUser.company.id,
        "userName" to matchingUser.name,
        "companyName" to matchingUser.company.name
    )
    )
}
}
