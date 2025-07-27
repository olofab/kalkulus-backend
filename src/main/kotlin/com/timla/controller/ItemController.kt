package com.timla.controller

import com.timla.dto.ItemUpdateRequest
import com.timla.model.Item
import com.timla.model.Offer
import com.timla.dto.ItemDTO
import com.timla.dto.AddItemRequest
import com.timla.mapper.ItemMapper
import com.timla.repository.ItemRepository
import com.timla.service.ItemService
import com.timla.repository.OfferRepository
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.timla.repository.ItemTemplateRepository


@RestController
@RequestMapping("/api")
class ItemController(
    private val offerRepository: OfferRepository,
    private val itemRepository: ItemRepository,
    private val itemService: ItemService,
    private val itemTemplateRepository: ItemTemplateRepository,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/offers/{offerId}/items")
    fun getItemsForOffer(
        @PathVariable offerId: Long,
        request: HttpServletRequest
    ): ResponseEntity<List<ItemDTO>> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val offer = offerRepository.findById(offerId).orElse(null)

            if (offer == null || offer.companyId != companyId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }

            val items = itemRepository.findByOfferId(offerId)
            val itemDtos = items.map { ItemMapper.toDto(it) }  // ✔️ riktig konvertering

            ResponseEntity.ok(itemDtos)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // 1. Legg til vare i et tilbud
    @PostMapping("/offers/{offerId}/item")
    fun addItem(
        @PathVariable offerId: Long,
        @RequestBody request: AddItemRequest,
        requestServlet: HttpServletRequest
    ): ResponseEntity<Offer> {
        val token = requestServlet.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val offer = offerRepository.findById(offerId)
                .filter { it.companyId == companyId }
                .orElseThrow { RuntimeException("Tilbud ikke funnet eller tilgang nektet") }

            val template = itemTemplateRepository.findByIdAndCompanyId(request.templateId, companyId)
                ?: return ResponseEntity.badRequest().build()

            val item = Item(
                name = template.name,
                quantity = request.quantity,
                unitPrice = template.unitPrice,
                offer = offer
            )
            
            item.offer = offer
            itemRepository.save(item)

            val updatedOffer = offerRepository.findById(offerId).get()
            ResponseEntity.ok(updatedOffer)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

     @PutMapping("/offers/{offerId}/items/{itemId}")
    fun updateItemQuantity(
        @PathVariable itemId: Long,
        @RequestBody @Valid request: ItemUpdateRequest,
        requestServlet: HttpServletRequest
    ): ResponseEntity<Item> {
        val token = requestServlet.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val item = itemRepository.findById(itemId).orElseThrow(){ RuntimeException("Vare ikke funnet") }

            val offer = item.offer
            if (offer == null || offer.companyId != companyId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }

            item.quantity = request.quantity
            ResponseEntity.ok(itemRepository.save(item))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @DeleteMapping("/offers/{offerId}/items/{itemId}")
    fun deleteItem(
        @PathVariable offerId: Long,
        @PathVariable itemId: Long,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val companyId = jwtUtil.getCompanyId(token)

        val offer = offerRepository.findById(offerId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        if (offer.companyId != companyId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val itemToRemove = offer.items.find { it.id == itemId }
            ?: return ResponseEntity.notFound().build()

        offer.items.remove(itemToRemove)
        offerRepository.save(offer) // sørg for at cascade = CascadeType.ALL er satt på items

        return ResponseEntity.noContent().build()
    }
}
