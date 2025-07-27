package com.timla.controller

import com.timla.dto.OfferResponse
import com.timla.dto.UpdateOfferRequest
import com.timla.model.Item
import com.timla.model.Offer
import com.timla.service.OfferService
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/offers")
class OfferController(
    private val offerService: OfferService,
    private val jwtUtil: JwtUtil
) {

    private fun getCompanyIdFromRequest(request: HttpServletRequest): Long {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: throw RuntimeException("Token mangler")
        return jwtUtil.getCompanyId(token)
    }

    @GetMapping
    fun getAllOffers(request: HttpServletRequest): List<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.getAllOffers(companyId)
    }

    @GetMapping("/{id}")
    fun getOfferById(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        val offer = offerService.getOfferById(id, companyId)
        return if (offer != null) ResponseEntity.ok(offer)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createOffer(@RequestBody request: Offer, httpRequest: HttpServletRequest): OfferResponse {
        val companyId = getCompanyIdFromRequest(httpRequest)
        return offerService.createOffer(request, companyId)
    }

   @PutMapping("/{id}")
    fun updateOffer(
        @PathVariable id: Long,
        @RequestBody updateOfferRequest: UpdateOfferRequest,
        request: HttpServletRequest
    ): ResponseEntity<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.updateOffer(id, updateOfferRequest, companyId)
    }

    @DeleteMapping("/{id}")
    fun deleteOffer(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<Void> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.deleteOffer(id, companyId)
    }

    @GetMapping("/search")
    fun searchOffers(
        @RequestParam query: String,
        request: HttpServletRequest
    ): List<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.searchOffers(query, companyId)
    }
}
