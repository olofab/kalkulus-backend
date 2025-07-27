package com.timla.controller

import com.timla.model.Company
import com.timla.repository.CompanyRepository
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/company")
class CompanyController(
    private val companyRepository: CompanyRepository,
    private val jwtUtil: JwtUtil
) {

    @PutMapping("/configure")
    fun configureCompany(
        @RequestBody config: Map<String, Double>,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).body("Token mangler")

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val company = companyRepository.findById(companyId).orElseThrow()

            company.hourlyRate = config["hourlyRate"] ?: company.hourlyRate
            company.machineRate = config["machineRate"] ?: company.machineRate
            company.fuelRate = config["fuelRate"] ?: company.fuelRate

            companyRepository.save(company)
            ResponseEntity.ok("Firma oppdatert")
        } catch (e: Exception) {
            ResponseEntity.status(400).body("Feil: ${e.message}")
        }
    }

    @GetMapping("/me")
    fun getCurrentCompany(request: HttpServletRequest): ResponseEntity<Company> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val company = companyRepository.findById(companyId).orElseThrow()
            ResponseEntity.ok(company)
        } catch (e: Exception) {
            ResponseEntity.status(404).build()
        }
    }
}
