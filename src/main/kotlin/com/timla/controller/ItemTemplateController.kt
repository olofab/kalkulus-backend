package com.timla.controller

import com.timla.model.ItemTemplate
import com.timla.dto.CreateItemTemplateRequest
import com.timla.repository.ItemTemplateRepository
import com.timla.repository.CategoryRepository
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/items/templates")
class ItemTemplateController(
    private val itemTemplateRepository: ItemTemplateRepository,
    private val categoryRepository: CategoryRepository,
    private val jwtUtil: JwtUtil
) {

    @GetMapping
    fun getAllTemplates(request: HttpServletRequest): ResponseEntity<List<ItemTemplate>> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ") ?: return ResponseEntity.status(401).build()
        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val items = itemTemplateRepository.findByCompanyId(companyId)
            ResponseEntity.ok(items)
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }

  @PostMapping
    fun createTemplate(
        @RequestBody request: CreateItemTemplateRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<ItemTemplate> {
        val token = httpRequest.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val categories = categoryRepository.findAllById(request.categoryIds).filter { it.companyId == companyId }

            val newItem = ItemTemplate(
                name = request.name,
                unitPrice = request.unitPrice,
                categories = categories.toMutableList(),
                companyId = companyId
            )

            val saved = itemTemplateRepository.save(newItem)
            ResponseEntity.ok(saved)
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }

    @PutMapping("/{id}")
    fun updateItemTemplate(
        @PathVariable id: Long,
        @RequestBody updatedData: ItemTemplate,
        request: HttpServletRequest
    ): ResponseEntity<ItemTemplate> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val existing = itemTemplateRepository.findByIdAndCompanyId(id, companyId)
                ?: return ResponseEntity.notFound().build()

            existing.name = updatedData.name
            existing.unitPrice = updatedData.unitPrice
            existing.categories = updatedData.categories

            val saved = itemTemplateRepository.save(existing)
            ResponseEntity.ok(saved)
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTemplate(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<Void> {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ") ?: return ResponseEntity.status(401).build()
        return try {
            val companyId = jwtUtil.getCompanyId(token)
            val template = itemTemplateRepository.findByIdAndCompanyId(id, companyId)
                ?: return ResponseEntity.notFound().build()
            itemTemplateRepository.delete(template)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.status(400).build()
        }
    }
}
