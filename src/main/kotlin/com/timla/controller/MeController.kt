package com.timla.controller

import com.timla.dto.MeResponse
import com.timla.dto.UserResponse
import com.timla.repository.CompanyRepository
import com.timla.repository.UserRepository
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/me")
class MeController(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val companyRepository: CompanyRepository
) {

    @GetMapping
    fun getMe(request: HttpServletRequest): ResponseEntity<MeResponse> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        return try {
            val userId = jwtUtil.getUserId(token)
            val companyId = jwtUtil.getCompanyId(token)

            val user = userRepository.findById(userId).orElseThrow()
            val company = companyRepository.findById(companyId).orElseThrow()

            val userResponse = UserResponse(
                id = user.id,
                name = user.name,
                email = user.email,
                userType = user.userType,
                companyId = user.company.id,
                isAdmin = user.isAdmin
            )

            ResponseEntity.ok(MeResponse(user = userResponse, company = company))
        } catch (e: Exception) {
            ResponseEntity.status(403).build()
        }
    }
}
