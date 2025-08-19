package com.timla.controller

import com.timla.dto.ItemUpdateRequest
import com.timla.dto.AddCustomItemRequest
import com.timla.dto.AddCustomItemResponse
import com.timla.dto.UpdateItemCategoryRequest
import com.timla.dto.UpdateItemCategoryResponse
import com.timla.dto.UpdateOfferItemRequest
import com.timla.dto.UpdateOfferItemResponse
import com.timla.dto.DeleteOfferItemResponse
import com.timla.model.Item
import com.timla.model.Offer
import com.timla.dto.ItemDTO
import com.timla.dto.AddItemRequest
import com.timla.mapper.ItemMapper
import com.timla.repository.ItemRepository
import com.timla.repository.CategoryRepository
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
    private val categoryRepository: CategoryRepository,
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
                categoryId = null, // Template items don't have category initially
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

    // Add item template to offer (frontend expected endpoint)
    @PostMapping("/offers/{offerId}/item/template")
    fun addTemplateToOffer(
        @PathVariable offerId: Long,
        @RequestBody request: AddItemRequest,
        requestServlet: HttpServletRequest
    ): ResponseEntity<Any> {
        val token = requestServlet.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            
            // Validate input
            if (request.quantity <= 0) {
                return ResponseEntity.badRequest().body(mapOf("error" to "Invalid quantity"))
            }
            
            // Find and validate offer access
            val offer = offerRepository.findById(offerId)
                .filter { it.companyId == companyId }
                .orElse(null) ?: return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(mapOf("error" to "Access denied"))

            // Find template and validate it belongs to the same company
            val template = itemTemplateRepository.findByIdAndCompanyId(request.templateId, companyId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Template not found"))

            // Create new item from template
            val item = Item(
                name = template.name,
                quantity = request.quantity,
                unitPrice = template.unitPrice,
                categoryId = null, // Template items don't have category initially
                offer = offer
            )
            
            val savedItem = itemRepository.save(item)

            // Return the item data that frontend expects
            val response = mapOf(
                "id" to savedItem.id,
                "name" to savedItem.name,
                "unitPrice" to savedItem.unitPrice,
                "quantity" to savedItem.quantity
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Server error: ${e.message}"))
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
    ): ResponseEntity<DeleteOfferItemResponse> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)

            val offer = offerRepository.findById(offerId).orElse(null)
                ?: return ResponseEntity.notFound().build()

            if (offer.companyId != companyId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }

            val itemToRemove = offer.items.find { it.id == itemId }
                ?: return ResponseEntity.notFound().build()

            offer.items.remove(itemToRemove)
            offerRepository.save(offer)

            val response = DeleteOfferItemResponse(
                success = true,
                message = "Item successfully deleted from offer"
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            val response = DeleteOfferItemResponse(
                success = false,
                message = "Failed to delete item: ${e.message}"
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

    // Add custom item (without template) to an offer
    @PostMapping("/offers/{offerId}/item/custom")
    fun addCustomItem(
        @PathVariable offerId: Long,
        @RequestBody request: AddCustomItemRequest,
        requestServlet: HttpServletRequest
    ): ResponseEntity<AddCustomItemResponse> {
        val token = requestServlet.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val offer = offerRepository.findById(offerId)
                .filter { it.companyId == companyId }
                .orElseThrow { RuntimeException("Tilbud ikke funnet eller tilgang nektet") }

            // Validate category if provided
            if (request.categoryId != null) {
                val category = categoryRepository.findByIdAndCompanyId(request.categoryId, companyId)
                    ?: return ResponseEntity.badRequest().build()
            }

            val item = Item(
                name = request.name,
                quantity = request.quantity,
                unitPrice = request.unitPrice,
                categoryId = request.categoryId,
                offer = offer
            )
            
            val savedItem = itemRepository.save(item)

            val response = AddCustomItemResponse(
                itemId = savedItem.id,
                name = savedItem.name,
                unitPrice = savedItem.unitPrice,
                quantity = savedItem.quantity
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // Update offer item (comprehensive update)
    @PutMapping("/offers/{offerId}/items/{itemId}/update")
    fun updateOfferItem(
        @PathVariable offerId: Long,
        @PathVariable itemId: Long,
        @RequestBody request: UpdateOfferItemRequest,
        requestServlet: HttpServletRequest
    ): ResponseEntity<UpdateOfferItemResponse> {
        val token = requestServlet.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            
            // Verify offer belongs to company
            val offer = offerRepository.findById(offerId)
                .filter { it.companyId == companyId }
                .orElse(null) ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            // Find item and verify it belongs to the offer
            val item = itemRepository.findById(itemId).orElse(null)
                ?: return ResponseEntity.notFound().build()

            if (item.offer?.id != offerId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }

            // Validate category if provided
            if (request.categoryId != null) {
                val category = categoryRepository.findByIdAndCompanyId(request.categoryId, companyId)
                    ?: return ResponseEntity.badRequest().build()
            }

            // Update item fields
            item.name = request.name
            item.quantity = request.quantity
            item.unitPrice = request.unitPrice
            item.categoryId = request.categoryId
            
            val savedItem = itemRepository.save(item)

            val response = UpdateOfferItemResponse(
                success = true,
                itemId = savedItem.id,
                name = savedItem.name,
                quantity = savedItem.quantity,
                unitPrice = savedItem.unitPrice,
                categoryId = savedItem.categoryId
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // Update item category (legacy endpoint - kept for backward compatibility)
    @PutMapping("/offers/{offerId}/item/{itemId}")
    fun updateItemCategory(
        @PathVariable offerId: Long,
        @PathVariable itemId: Long,
        @RequestBody request: UpdateItemCategoryRequest,
        requestServlet: HttpServletRequest
    ): ResponseEntity<UpdateItemCategoryResponse> {
        val token = requestServlet.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            
            // Verify offer belongs to company
            val offer = offerRepository.findById(offerId)
                .filter { it.companyId == companyId }
                .orElse(null) ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            // Find item and verify it belongs to the offer
            val item = itemRepository.findById(itemId).orElse(null)
                ?: return ResponseEntity.notFound().build()

            if (item.offer?.id != offerId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }

            // Validate category belongs to company
            val category = categoryRepository.findByIdAndCompanyId(request.categoryId, companyId)
                ?: return ResponseEntity.badRequest().build()

            // Update item category
            item.categoryId = request.categoryId
            itemRepository.save(item)

            val response = UpdateItemCategoryResponse(
                success = true,
                itemId = itemId,
                categoryId = request.categoryId
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }
}
